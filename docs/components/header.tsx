"use client";
import { ModeToggle } from "./mode-toggle";
import { Github, Clock } from "lucide-react";
import Image from "next/image";

export default function Header() {
  return (
    <header className="relative overflow-hidden py-12 px-4 text-center text-white monochrome-gradient shadow-md">
      <div className="absolute inset-0 flex items-center justify-center z-0">
        <div className="w-[200px] h-[200px] opacity-10 animate-[backgroundAnimation_30s_linear_infinite]">
          <Image
            src="/icon.svg"
            alt="Compose Logo"
            width={200}
            height={200}
            loading="eager"
            style={{ width: "auto", height: "auto" }}
          />
        </div>
      </div>

      <div className="relative z-10 flex flex-col items-center gap-6">
        <div>
          <h1 className="text-4xl font-extrabold mb-2 tracking-tight text-shadow">
            Compose for Desktop Wizard
          </h1>
          <p className="text-lg opacity-90 max-w-2xl mx-auto">
            Generate a production-ready Kotlin Compose for Desktop project in
            seconds
          </p>
        </div>

        <div className="flex items-center justify-center gap-4 mt-2 md:absolute md:right-0 md:top-[-50px]">
          <ModeToggle />
          <div className="flex gap-4">
            <a
              href="https://github.com/zahid4kh/compose-for-desktop"
              target="_blank"
              className="text-white opacity-70 hover:opacity-100 transition-opacity"
              title="GitHub Repository"
              rel="noreferrer"
            >
              <Github size={24} />
            </a>
            <a
              href="https://github.com/zahid4kh/compose-for-desktop/wiki"
              target="_blank"
              className="text-white opacity-70 hover:opacity-100 transition-opacity"
              title="GitHub Wiki"
              rel="noreferrer"
            >
              <Clock size={24} />
            </a>
          </div>
        </div>
      </div>
    </header>
  );
}
