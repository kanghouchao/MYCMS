'use client';

import { useState, useEffect, Suspense } from 'react';
import Image from 'next/image';
import { useRouter, useSearchParams } from 'next/navigation';
import { authApi } from '@/services/tenant/api';

export default function RegisterPage() {
  return (
    <Suspense>
      <RegisterForm />
    </Suspense>
  );
}

function RegisterForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [token, setToken] = useState<string>('');
  const [email, setEmail] = useState<string>('');

  useEffect(() => {
    setToken(searchParams?.get('token') || '');
  }, [searchParams]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    if (!token) {
      setError(
        'リンクが無効、またはトークンが不足しています。メールのリンクから再度アクセスしてください。'
      );
      return;
    }
    if (password.length < 8) {
      setError('パスワードは8文字以上で入力してください。');
      return;
    }
    if (password !== confirmPassword) {
      setError('パスワードが一致しません。もう一度ご確認ください。');
      return;
    }
    try {
      await authApi.register({ token, email, password });
      setSuccess('登録が完了しました。管理画面にログインしてください。');
      setTimeout(() => router.push('/login'), 2000);
    } catch (err) {
      setError('ネットワークエラーが発生しました');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-200">
      <div className="w-full max-w-md p-8 bg-white rounded-xl shadow-lg border border-blue-100">
        <div className="flex items-center mb-6">
          {/* Next.js Image 优化图片加载 */}
          <Image
            src="/images/logos/32.svg"
            alt="Oli-CMS"
            width={40}
            height={40}
            className="h-10 mr-3"
          />
          <h2 className="text-2xl font-bold text-blue-700">Oli-CMS 管理者登録</h2>
        </div>
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
              メールアドレス
            </label>
            <input
              type="email"
              name="email"
              id="email"
              placeholder="メールアドレス"
              value={email}
              onChange={e => setEmail(e.target.value)}
              className="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
              required
            />
          </div>
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
              ログイン用パスワード設定
            </label>
            <input
              type="password"
              name="password"
              id="password"
              placeholder="パスワード（8文字以上）"
              value={password}
              onChange={e => setPassword(e.target.value)}
              className="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
              required
              minLength={8}
            />
          </div>
          <div>
            <label
              htmlFor="confirm-password"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              パスワード（確認用）
            </label>
            <input
              type="password"
              name="confirm-password"
              id="confirm-password"
              placeholder="パスワード（確認用）"
              value={confirmPassword}
              onChange={e => setConfirmPassword(e.target.value)}
              className="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
              required
              minLength={8}
            />
          </div>
          <button
            type="submit"
            className="w-full py-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded transition"
          >
            登録を完了する
          </button>
        </form>
        {error && <div className="text-red-500 mt-4 text-center">{error}</div>}
        {success && <div className="text-green-600 mt-4 text-center">{success}</div>}
        <div className="mt-8 text-xs text-gray-400 text-center">
          ご不明点はOli-CMSサポートまでご連絡ください
        </div>
      </div>
    </div>
  );
}
