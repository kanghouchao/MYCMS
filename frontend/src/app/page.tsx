import { cookies } from 'next/headers';
import { notFound, redirect } from 'next/navigation';

export default async function Home() {
  const cookieStore = await cookies();
  const role = cookieStore.get('x-mw-role')?.value;

  // 这个页面应该根据登录信息和当前中间件的逻辑进行重定向
  // 如果判定是平台管理员，直接跳转到平台管理员首页
  if (role === 'central') {
    redirect('/central/dashboard/central/');
  }

  /**
   * TODO 如果判定是租户，就得看是访问管理员页面还是访问前端页面
   * 如果没有登录信息，则直接访问前端首页
   * 如果有登录信息，则需要查看是否访问管理员页面 
   */
  if (role === 'tenant') {
    const templateKey = cookieStore.get('x-mw-tenant-template')?.value || 'default';
    try {
      const TemplateComponent = require(`@/app/tenant/templates/${templateKey}/page`).default;
      return <TemplateComponent />;
    } catch (e) {
      console.error('Template not found:', e);
      notFound();
    }
  }

  notFound();
}
