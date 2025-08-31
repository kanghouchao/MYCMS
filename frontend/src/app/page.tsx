import { headers } from "next/headers";
import { redirect } from "next/navigation";

export default function Home() {
  const role = headers().get("x-mw-role");

  if (role === "admin") {
    redirect("/central/dashboard");
  }

  if (role === "tenant") {
    const templateKey = headers().get("x-mw-tenant-template") || "default";
    try {
      const TemplateComponent =
        require(`@/app/tenant/templates/${templateKey}/page`).default;
      return <TemplateComponent />;
    } catch (e) {
      console.error("Template not found:", e);
      redirect("/404");
    }
  }

  redirect("/404");
}
