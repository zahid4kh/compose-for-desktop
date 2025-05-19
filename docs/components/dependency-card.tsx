"use client"

import type React from "react"
import { Badge } from "@/components/ui/badge"

interface DependencyCardProps {
  id: string
  title: string
  description: string
  badge: string
  checked: boolean
  onChange: (id: string, checked: boolean) => void
}

export default function DependencyCard({ id, title, description, badge, checked, onChange }: DependencyCardProps) {
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange(id, e.target.checked)
  }

  return (
    <label
      htmlFor={id}
      className={`
        relative flex flex-col h-full p-4 rounded-lg border cursor-pointer transition-all z-10
        ${checked ? "border-primary bg-primary/5 dark:bg-primary/10 white-glow" : "border-border"}
        hover:border-primary hover:-translate-y-1 hover:shadow-md
      `}
    >
      <input type="checkbox" id={id} checked={checked} onChange={handleChange} className="sr-only" />

      <div className="absolute top-3 right-3 w-5 h-5 rounded-full border-2 border-border flex items-center justify-center transition-all pointer-events-none">
        {checked && <div className="w-2 h-2 rounded-full bg-primary"></div>}
      </div>

      <div className="mb-2 pointer-events-none">
        <div className="font-semibold">{title}</div>
      </div>

      <div className="text-sm text-muted-foreground flex-grow pointer-events-none">{description}</div>

      <div className="mt-2 pointer-events-none">
        <Badge variant="outline" className="bg-primary/10 text-primary text-xs">
          {badge}
        </Badge>
      </div>
    </label>
  )
}
