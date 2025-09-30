import React from 'react';
import { render, waitFor } from '@testing-library/react';
import { AuthProvider, useAuth } from '@/contexts/AuthContext';
import Cookies from 'js-cookie';

import { authApi } from '@/services/central/api';
import { isTenantDomain } from '@/lib/config';

jest.mock('js-cookie');

const mockPush = jest.fn();
jest.mock('next/navigation', () => ({ useRouter: () => ({ push: mockPush }) }));

jest.mock('@/services/central/api', () => ({
  authApi: { logout: jest.fn() },
}));

jest.mock('@/lib/config', () => ({
  isTenantDomain: jest.fn(),
}));

function Consumer() {
  const { logout } = useAuth();
  return (<button onClick={() => logout()}>out</button>) as any;
}

describe('AuthProvider', () => {
  afterEach(() => {
    jest.clearAllMocks();
    (Cookies.get as jest.Mock).mockReset?.();
    (isTenantDomain as jest.Mock).mockReset?.();
    window.history.replaceState({}, '', '/');
  });

  it('redirects to login if no token on mount', async () => {
    // Ensure provider treats this as central domain so we don't hit tenant APIs
    (isTenantDomain as jest.Mock).mockReturnValue(false);
    (Cookies.get as jest.Mock).mockReturnValue(undefined);
    render(
      <AuthProvider>
        <div />
      </AuthProvider>
    );
    await waitFor(() => expect(mockPush).toHaveBeenCalledWith('/login'));
  });

  it('logout calls api and removes token and navigates', async () => {
    // Ensure provider treats this as central domain so logout calls mocked central api
    (isTenantDomain as jest.Mock).mockReturnValue(false);
    (Cookies.get as jest.Mock).mockReturnValue('tkn');
    (authApi.logout as jest.Mock).mockResolvedValueOnce({});
    const removeSpy = jest.spyOn(Cookies, 'remove');
    const { getByText } = render(
      <AuthProvider>
        <Consumer />
      </AuthProvider>
    );
    getByText('out').click();
    await waitFor(() => expect(authApi.logout).toHaveBeenCalled());
    expect(removeSpy).toHaveBeenCalledWith('token');
    expect(mockPush).toHaveBeenCalledWith('/login');
  });

  it('does not redirect on public register route', () => {
    (isTenantDomain as jest.Mock).mockReturnValue(false);
    (Cookies.get as jest.Mock).mockReturnValue(undefined);
    window.history.replaceState({}, '', '/register');

    render(
      <AuthProvider>
        <div />
      </AuthProvider>
    );

    expect(mockPush).not.toHaveBeenCalled();
  });
});
