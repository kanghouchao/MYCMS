'use client';

import { ChangeEvent, FormEvent, useCallback, useEffect, useMemo, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '@/contexts/AuthContext';
import { employeeApi } from '@/services/tenant/api';
import { CreateEmployeeRequest, Employee } from '@/types/api';

interface TenantDashboardProps {
  tenantId?: string | null;
  tenantName?: string | null;
}

interface FormState {
  name: string;
  email: string;
  phone: string;
  position: string;
}

const initialFormState: FormState = {
  name: '',
  email: '',
  phone: '',
  position: '',
};

function trimOrUndefined(value: string): string | undefined {
  const trimmed = value.trim();
  return trimmed.length === 0 ? undefined : trimmed;
}

function formatDateTime(value?: string | null): string {
  if (!value) {
    return '-';
  }
  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) {
    return value;
  }
  return parsed.toLocaleString('ja-JP');
}

export default function TenantDashboard({ tenantId, tenantName }: TenantDashboardProps) {
  const { logout } = useAuth();
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formState, setFormState] = useState<FormState>(initialFormState);

  const safeTenantName = useMemo(() => tenantName?.trim() || 'テナント', [tenantName]);

  const loadEmployees = useCallback(async () => {
    setIsLoading(true);
    try {
      const data = await employeeApi.list();
      setEmployees(data);
    } catch (error) {
      console.error('Failed to load employees', error);
      toast.error('従業員一覧の取得に失敗しました');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadEmployees();
  }, [loadEmployees]);

  const handleInputChange = useCallback((field: keyof FormState) => {
    return (event: ChangeEvent<HTMLInputElement>) => {
      setFormState(prev => ({ ...prev, [field]: event.target.value }));
    };
  }, []);

  const handleSubmit = useCallback(
    async (event: FormEvent<HTMLFormElement>) => {
      event.preventDefault();
      const name = formState.name.trim();
      if (!name) {
        toast.error('従業員名を入力してください');
        return;
      }

      const payload: CreateEmployeeRequest = {
        name,
        email: trimOrUndefined(formState.email),
        phone: trimOrUndefined(formState.phone),
        position: trimOrUndefined(formState.position),
      };

      setIsSubmitting(true);
      try {
        const created = await employeeApi.create(payload);
        setEmployees(prev => [created, ...prev]);
        setFormState({ ...initialFormState });
        toast.success('従業員を追加しました');
      } catch (error) {
        console.error('Failed to create employee', error);
        toast.error('従業員の追加に失敗しました');
      } finally {
        setIsSubmitting(false);
      }
    },
    [formState]
  );

  return (
    <main className="min-h-screen bg-slate-50 text-slate-900">
      <header className="bg-white shadow-sm">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
          <div>
            <h1 className="text-xl font-semibold text-indigo-700">{safeTenantName}・従業員管理</h1>
            <p className="text-sm text-slate-500">
              日々の運用に必要なスタッフ情報をここから登録・確認できます
            </p>
          </div>
          <div className="flex items-center gap-3">
            {tenantId && (
              <span className="rounded-full bg-indigo-50 px-3 py-1 text-xs font-medium text-indigo-600">
                Tenant ID: {tenantId}
              </span>
            )}
            <button
              type="button"
              onClick={logout}
              className="rounded-md border border-slate-200 px-4 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100"
            >
              ログアウト
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto flex max-w-6xl flex-col gap-6 px-4 py-8 sm:px-6 lg:px-8">
        <section className="rounded-lg bg-white p-6 shadow-sm">
          <h2 className="text-lg font-semibold text-slate-800">従業員を追加</h2>
          <p className="mt-1 text-sm text-slate-500">
            スタッフ名と連絡先を登録すると、シフト管理やリソース割当の準備が整います
          </p>

          <form className="mt-6 grid gap-4 md:grid-cols-2" onSubmit={handleSubmit}>
            <div className="md:col-span-1">
              <label htmlFor="employee-name" className="block text-sm font-medium text-slate-700">
                従業員名<span className="ml-1 text-red-500">*</span>
              </label>
              <input
                id="employee-name"
                type="text"
                value={formState.name}
                onChange={handleInputChange('name')}
                placeholder="例：山田 太郎"
                className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div className="md:col-span-1">
              <label htmlFor="employee-email" className="block text-sm font-medium text-slate-700">
                メールアドレス
              </label>
              <input
                id="employee-email"
                type="email"
                value={formState.email}
                onChange={handleInputChange('email')}
                placeholder="例：staff@example.com"
                className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div className="md:col-span-1">
              <label htmlFor="employee-phone" className="block text-sm font-medium text-slate-700">
                電話番号
              </label>
              <input
                id="employee-phone"
                type="tel"
                value={formState.phone}
                onChange={handleInputChange('phone')}
                placeholder="例：090-1234-5678"
                className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div className="md:col-span-1">
              <label
                htmlFor="employee-position"
                className="block text-sm font-medium text-slate-700"
              >
                役職 / 担当
              </label>
              <input
                id="employee-position"
                type="text"
                value={formState.position}
                onChange={handleInputChange('position')}
                placeholder="例：店長 / スタイリスト"
                className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div className="md:col-span-2 flex items-center justify-end gap-3">
              <button
                type="button"
                onClick={() => setFormState({ ...initialFormState })}
                className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100"
                disabled={isSubmitting}
              >
                リセット
              </button>
              <button
                type="submit"
                disabled={isSubmitting}
                className="rounded-md bg-indigo-600 px-5 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isSubmitting ? '登録中…' : '従業員を登録'}
              </button>
            </div>
          </form>
        </section>

        <section className="rounded-lg bg-white p-6 shadow-sm">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-lg font-semibold text-slate-800">従業員一覧</h2>
              <p className="text-sm text-slate-500">スタッフの連絡先や役割を確認できます</p>
            </div>
            <span className="text-sm text-slate-500">{employees.length} 名登録済み</span>
          </div>

          {isLoading ? (
            <div className="mt-8 flex flex-col items-center justify-center gap-3 text-slate-500">
              <div className="h-8 w-8 animate-spin rounded-full border-2 border-indigo-200 border-t-indigo-600" />
              <span className="text-sm">読み込み中です…</span>
            </div>
          ) : employees.length === 0 ? (
            <div className="mt-8 rounded-md border border-dashed border-slate-200 bg-slate-50 p-8 text-center text-sm text-slate-500">
              まだ従業員が登録されていません。上のフォームから最初のスタッフを追加しましょう。
            </div>
          ) : (
            <div className="mt-6 overflow-hidden rounded-lg border border-slate-200">
              <table className="min-w-full divide-y divide-slate-200">
                <thead className="bg-slate-50">
                  <tr>
                    <th
                      scope="col"
                      className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wide text-slate-500"
                    >
                      氏名
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wide text-slate-500"
                    >
                      連絡先
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wide text-slate-500"
                    >
                      役職
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wide text-slate-500"
                    >
                      状態
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wide text-slate-500"
                    >
                      登録日時
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200 bg-white">
                  {employees.map(employee => (
                    <tr key={employee.id}>
                      <td className="whitespace-nowrap px-4 py-3 text-sm font-medium text-slate-800">
                        {employee.name}
                      </td>
                      <td className="whitespace-nowrap px-4 py-3 text-sm text-slate-600">
                        <div className="flex flex-col">
                          <span>{employee.email || '-'}</span>
                          <span className="text-xs text-slate-400">{employee.phone || '-'}</span>
                        </div>
                      </td>
                      <td className="whitespace-nowrap px-4 py-3 text-sm text-slate-600">
                        {employee.position || '-'}
                      </td>
                      <td className="whitespace-nowrap px-4 py-3 text-sm">
                        <span
                          className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${
                            employee.active
                              ? 'bg-emerald-100 text-emerald-700'
                              : 'bg-slate-100 text-slate-500'
                          }`}
                        >
                          {employee.active ? '稼働中' : '停止'}
                        </span>
                      </td>
                      <td className="whitespace-nowrap px-4 py-3 text-sm text-slate-600">
                        {formatDateTime(employee.created_at)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>
    </main>
  );
}
