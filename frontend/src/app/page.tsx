import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

export default async function Home() {
  const cookieStore = await cookies();
  const role = cookieStore.get('x-mw-role')?.value;

  console.log('🏠 Home page - Role from cookie:', role);

  if (role === 'central') {
    redirect('/central/dashboard');
  }

  if (role === 'tenant') {
    const templateKey = cookieStore.get('x-mw-tenant-template')?.value || 'default';
    console.log('🎨 Template key:', templateKey);
    try {
      const TemplateComponent = require(`@/app/tenant/templates/${templateKey}/page`).default;
      return <TemplateComponent />;
    } catch (e) {
      console.error('Template not found:', e);
      redirect('/404');
    }
  }

  redirect('/404');
}
