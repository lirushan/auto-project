export default function HomePage() {
  return (
    <div className="flex flex-col items-center justify-center py-16">
      <h2 className="text-3xl font-bold tracking-tight">Auto Project</h2>
      <p className="mt-4 text-muted-foreground">
        AI-driven full-stack application powered by WorkBuddy
      </p>
      <div className="mt-8 flex gap-4">
        <div className="rounded-lg border bg-card p-6 text-center">
          <p className="text-sm font-medium">Frontend</p>
          <p className="mt-1 text-xs text-muted-foreground">React 19 + Vite + Tailwind CSS</p>
        </div>
        <div className="rounded-lg border bg-card p-6 text-center">
          <p className="text-sm font-medium">Backend</p>
          <p className="mt-1 text-xs text-muted-foreground">Spring Boot + Java 21 + MyBatis-Plus</p>
        </div>
        <div className="rounded-lg border bg-card p-6 text-center">
          <p className="text-sm font-medium">Pipeline</p>
          <p className="mt-1 text-xs text-muted-foreground">Daily 02:00 auto dev cycle</p>
        </div>
      </div>
    </div>
  )
}
