"use client"
import { useEffect, useState } from "react"
import { Moon, Sun } from "lucide-react"
import { useTheme } from "next-themes"

export function ModeToggle() {
  const { setTheme, theme, resolvedTheme } = useTheme()
  const [mounted, setMounted] = useState(false)

  // Only show the toggle after mounting to avoid hydration mismatch
  useEffect(() => {
    setMounted(true)
  }, [])

  if (!mounted) {
    return (
      <div className="flex items-center gap-2">
        <span className="text-sm font-medium text-white">Light</span>
        <div className="h-8 w-14 rounded-full bg-white/30 border-none relative">
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="h-4 w-4 bg-white rounded-full"></div>
          </div>
        </div>
        <span className="text-sm font-medium text-white">Dark</span>
      </div>
    )
  }

  const isDark = theme === "dark" || resolvedTheme === "dark"

  return (
    <div className="flex items-center gap-2">
      <span className="text-sm font-medium text-white">Light</span>
      <button
        onClick={() => setTheme(isDark ? "light" : "dark")}
        className="h-8 w-14 rounded-full bg-white/30 border-none hover:bg-white/40 relative transition-colors"
        aria-label="Toggle theme"
      >
        <div
          className={`absolute top-1 h-6 w-6 rounded-full bg-white transition-all duration-300 flex items-center justify-center
            ${isDark ? "left-7" : "left-1"}`}
        >
          {isDark ? <Moon className="h-3 w-3 text-black" /> : <Sun className="h-3 w-3 text-black" />}
        </div>
      </button>
      <span className="text-sm font-medium text-white">Dark</span>
    </div>
  )
}
