"use client"

import { useEffect, useState } from "react"

interface MarkdownPreviewProps {
  markdown: string
}

export default function MarkdownPreview({ markdown }: MarkdownPreviewProps) {
  const [html, setHtml] = useState<string>("")
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
    // Simple Markdown to HTML conversion
    const convertMarkdownToHtml = (md: string) => {
      // Process code blocks
      let processed = md.replace(/```(\w+)?\n([\s\S]*?)```/g, (match, language, code) => {
        const lang = language || ""
        const escapedCode = code.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
        return `<pre class="code-block ${lang}"><code>${escapedCode}</code></pre>`
      })

      // Process inline code
      processed = processed.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>')

      // Process headings
      processed = processed.replace(/^# (.*$)/gm, '<h1 class="text-2xl font-bold mt-6 mb-4">$1</h1>')
      processed = processed.replace(/^## (.*$)/gm, '<h2 class="text-xl font-bold mt-5 mb-3">$1</h2>')
      processed = processed.replace(/^### (.*$)/gm, '<h3 class="text-lg font-bold mt-4 mb-2">$1</h3>')

      // Process lists
      processed = processed.replace(/^\* (.*$)/gm, '<li class="ml-6 mb-1">$1</li>')
      processed = processed.replace(/^- (.*$)/gm, '<li class="ml-6 mb-1">$1</li>')
      processed = processed.replace(/<\/li>\n<li/g, "</li><li")
      processed = processed.replace(/<li(.*?)>([\s\S]*?)(?=<li|$)/g, '<ul class="list-disc mb-4"><li$1>$2</li></ul>')
      processed = processed.replace(/<\/ul>\n<ul[^>]*>/g, "")

      // Process links
      processed = processed.replace(
        /\[([^\]]+)\]$$([^)]+)$$/g,
        '<a href="$2" class="text-blue-400 hover:underline" target="_blank" rel="noopener noreferrer">$1</a>',
      )

      // Process paragraphs
      processed = processed.replace(/^(?!<[a-z])(.*$)/gm, (match) => {
        if (match.trim() === "") return match
        return `<p class="mb-4">${match}</p>`
      })

      // Process bold
      processed = processed.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>")

      // Process italic
      processed = processed.replace(/\*([^*]+)\*/g, "<em>$1</em>")

      return processed
    }

    setHtml(convertMarkdownToHtml(markdown))
  }, [markdown])

  if (!mounted) {
    return <div className="bg-[#1e1e1e] rounded-md p-4 h-full overflow-auto text-white">Loading...</div>
  }

  return (
    <div className="bg-[#1e1e1e] rounded-md p-4 h-full overflow-auto text-white markdown-content">
      <div dangerouslySetInnerHTML={{ __html: html }} />
      <style jsx global>{`
        .markdown-content .code-block {
          background-color: #2d2d2d;
          border-radius: 4px;
          padding: 1rem;
          margin: 1rem 0;
          overflow-x: auto;
          font-family: monospace;
        }
        .markdown-content .inline-code {
          background-color: #2d2d2d;
          border-radius: 3px;
          padding: 0.2rem 0.4rem;
          font-family: monospace;
        }
      `}</style>
    </div>
  )
}
