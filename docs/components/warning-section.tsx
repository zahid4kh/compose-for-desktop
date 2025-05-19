"use client";
import { Check, Copy } from "lucide-react";
import { useState } from "react";
import { Button } from "./ui/button";

type CopyButtonProps = {
  value: string;
  onCopy: (text: string) => Promise<void>;
  copied: boolean;
};

const CopyButton = ({ value, onCopy, copied }: CopyButtonProps) => (
  <Button
    type="button"
    variant="ghost"
    size="icon"
    className="h-8 w-8 hover:bg-slate-200 dark:hover:bg-slate-800"
    onClick={() => onCopy(value)}
  >
    {copied ? (
      <Check className="h-4 w-4 text-green-500" />
    ) : (
      <Copy className="h-4 w-4" />
    )}
    <span className="sr-only">Copy command</span>
  </Button>
);

export default function WarningSection() {
  const [copied, setCopied] = useState(false);

  const handleCopy = async (text: string) => {
    await navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-lg p-6 shadow-md animate-fadeInUp">
      <h2 className="text-xl font-bold mb-4 text-slate-900 dark:text-slate-100">
        Important: Before Running Your Project
      </h2>
      <p className="mb-4 text-slate-600 dark:text-slate-400">
        After extracting your project, you'll need to make the Gradle wrapper
        executable on Unix-based systems (Linux and macOS). This is required
        because ZIP files don't preserve executable permissions.
      </p>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
        <div className="bg-white dark:bg-slate-950 p-4 rounded-md border border-slate-200 dark:border-slate-800">
          <h3 className="font-semibold mb-2 flex items-center">
            <span className="mr-2">🐧 Linux / 🍎 macOS</span>
          </h3>
          <p className="text-sm mb-2 text-slate-600 dark:text-slate-400">
            Open terminal in your project directory and run:
          </p>
          <div className="bg-slate-100 dark:bg-slate-900 p-2 rounded font-mono text-sm mb-2 flex items-center justify-between">
            <code>chmod +x gradlew</code>
            <CopyButton
              value="chmod +x gradlew"
              onCopy={handleCopy}
              copied={copied}
            />
          </div>
          <p className="text-xs text-slate-500">
            This makes the Gradle wrapper script executable
          </p>
        </div>

        <div className="bg-white dark:bg-slate-950 p-4 rounded-md border border-slate-200 dark:border-slate-800">
          <h3 className="font-semibold mb-2 flex items-center">
            <span className="mr-2">🪟 Windows</span>
          </h3>
          <p className="text-sm mb-2 text-slate-600 dark:text-slate-400">
            No action needed! Windows uses the{" "}
            <code className="bg-slate-100 dark:bg-slate-900 px-1 rounded">
              gradlew.bat
            </code>{" "}
            file which doesn't require executable permissions.
          </p>
          <p className="text-xs text-slate-500">
            You can run your project directly
          </p>
        </div>
      </div>
    </div>
  );
}
