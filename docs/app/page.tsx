"use client";

import { useState } from "react";
import Header from "@/components/header";
import Footer from "@/components/footer";
import ProjectForm from "@/components/project-form";
import GeneratingOverlay from "@/components/generating-overlay";
import type { ProjectOptions } from "@/lib/types";
import { generateProject } from "@/lib/project-generator";
// stuff
export default function Home() {
  const [isGenerating, setIsGenerating] = useState(false);

  const handleGenerateProject = async (options: ProjectOptions) => {
    setIsGenerating(true);
    try {
      await generateProject(options);
    } catch (error) {
      console.error("Error generating project:", error);
      alert(
        "An error occurred while generating the project. Please try again."
      );
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow py-12">
        <div className="max-w-[1800px] mx-auto px-4">
          <ProjectForm onSubmit={handleGenerateProject} />
        </div>
      </main>
      <Footer />
      {isGenerating && <GeneratingOverlay />}
    </div>
  );
}
