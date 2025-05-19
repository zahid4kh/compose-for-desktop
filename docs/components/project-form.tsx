"use client";

import type React from "react";
import { useState, useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import DependencyCard from "@/components/dependency-card";
import CodePreview from "@/components/code-preview";
import MarkdownPreview from "@/components/markdown-preview";
import WarningSection from "@/components/warning-section";
import type { ProjectOptions } from "@/lib/types";
import {
  generateBuildGradlePreview,
  generateSettingsGradlePreview,
  generateMainFilePreview,
  generateVersionCatalogPreview,
  generateReadmePreview,
} from "@/lib/preview-functions";

export default function ProjectForm({
  onSubmit,
}: {
  onSubmit: (options: ProjectOptions) => void;
}) {
  const [options, setOptions] = useState<ProjectOptions>({
    appName: "",
    packageName: "",
    projectVersion: "1.0.0",
    windowWidth: "800",
    windowHeight: "600",
    includeRetrofit: false,
    includeDeskit: true,
    includeSQLDelight: false,
    includeKtor: false,
    includeDecompose: false,
    includeImageLoader: false,
    includePrecompose: false,
    includeSentry: false,
    includeMarkdown: false,
    includeHotReload: true,
    includeKotlinxDatetime: false,
  });

  const [gradlePreview, setGradlePreview] = useState("");
  const [settingsPreview, setSettingsPreview] = useState("");
  const [versionsPreview, setVersionsPreview] = useState("");
  const [mainPreview, setMainPreview] = useState("");
  const [readmePreview, setReadmePreview] = useState("");
  const [packageNameError, setPackageNameError] = useState("");
  const [isValid, setIsValid] = useState(true);

  useEffect(() => {
    updatePreviews();
  }, [options]);

  useEffect(() => {
    validatePackageName(options.packageName);
  }, [options.packageName]);

  const validatePackageName = (packageName: string) => {
    if (packageName.includes(" ")) {
      setPackageNameError(
        "Package name cannot contain spaces. Use dots instead (e.g., com.example.myapp)"
      );
      setIsValid(false);
    } else {
      setPackageNameError("");
      setIsValid(true);
    }
  };

  const updatePreviews = () => {
    setGradlePreview(generateBuildGradlePreview(options));
    setSettingsPreview(generateSettingsGradlePreview(options));
    setVersionsPreview(generateVersionCatalogPreview(options));
    setMainPreview(generateMainFilePreview(options));
    setReadmePreview(generateReadmePreview(options));
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value, type, checked } = e.target;
    setOptions((prev) => ({
      ...prev,
      [id]: type === "checkbox" ? checked : value,
    }));
  };

  const handleDependencyChange = (id: string, checked: boolean) => {
    setOptions((prev) => ({
      ...prev,
      [`include${id.charAt(0).toUpperCase() + id.slice(1)}`]: checked,
    }));
  };

  const handlePackageNameBlur = () => {
    if (options.packageName.includes(" ")) {
      const fixedPackageName = options.packageName.replace(/\s+/g, ".");
      setOptions((prev) => ({
        ...prev,
        packageName: fixedPackageName,
      }));
      validatePackageName(fixedPackageName);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isValid) {
      alert("Please fix all validation errors before generating the project.");
      return;
    }
    if (!options.appName || !options.packageName) {
      alert("Please fill out all required fields");
      return;
    }
    onSubmit(options);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-8 w-full mx-auto px-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <Card className="p-6 shadow-sm hover:shadow-md hover:-translate-y-1 transition-all">
          <h2 className="text-xl font-semibold text-primary mb-6 pb-2 border-b border-border relative after:content-[''] after:absolute after:bottom-[-2px] after:left-0 after:w-20 after:h-0.5 after:bg-accent hover:after:w-30 after:transition-all">
            Project Information
          </h2>

          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="appName">Application Name*</Label>
              <Input
                id="appName"
                value={options.appName}
                onChange={handleInputChange}
                placeholder="MyComposeApp"
                required
              />
              <p className="text-sm text-muted-foreground">
                Used for window title and app name
              </p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="packageName">Package Name*</Label>
              <Input
                id="packageName"
                value={options.packageName}
                onChange={handleInputChange}
                onBlur={handlePackageNameBlur}
                placeholder="com.example.myapp"
                required
                className={packageNameError ? "border-red-500" : ""}
              />
              {packageNameError && (
                <p className="text-sm text-red-500">{packageNameError}</p>
              )}
              <p className="text-sm text-muted-foreground">
                E.g. com.yourdomain.appname
              </p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="projectVersion">Version</Label>
              <Input
                id="projectVersion"
                value={options.projectVersion}
                onChange={handleInputChange}
                placeholder="1.0.0"
              />
            </div>
          </div>
        </Card>

        <Card className="p-6 shadow-sm hover:shadow-md hover:-translate-y-1 transition-all">
          <h2 className="text-xl font-semibold text-primary mb-6 pb-2 border-b border-border relative after:content-[''] after:absolute after:bottom-[-2px] after:left-0 after:w-20 after:h-0.5 after:bg-accent hover:after:w-30 after:transition-all">
            UI Configuration
          </h2>

          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="windowWidth">Default Window Width (dp)</Label>
              <Input
                id="windowWidth"
                type="number"
                value={options.windowWidth}
                onChange={handleInputChange}
                min="400"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="windowHeight">Default Window Height (dp)</Label>
              <Input
                id="windowHeight"
                type="number"
                value={options.windowHeight}
                onChange={handleInputChange}
                min="300"
              />
            </div>
          </div>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <Card className="p-6 shadow-sm hover:shadow-md hover:-translate-y-1 transition-all h-[600px] overflow-y-auto">
          <h2 className="text-xl font-semibold text-primary mb-6 pb-2 border-b border-border relative after:content-[''] after:absolute after:bottom-[-2px] after:left-0 after:w-20 after:h-0.5 after:bg-accent hover:after:w-30 after:transition-all">
            Additional Dependencies
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
            <DependencyCard
              id="HotReload"
              title="Hot Reload"
              description="Enable live code updates without restarting the application"
              badge="Compose"
              checked={options.includeHotReload}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="Deskit"
              title="Deskit"
              description="Material3 FileChooser and other dialog components"
              badge="UI"
              checked={options.includeDeskit}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="Retrofit"
              title="Retrofit + OkHttp"
              description="Type-safe HTTP client for API calls"
              badge="Network"
              checked={options.includeRetrofit}
              onChange={handleDependencyChange}
            />
            <DependencyCard
              id="SQLDelight"
              title="SQLDelight"
              description="Type-safe SQL for local database storage"
              badge="Database"
              checked={options.includeSQLDelight}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="Ktor"
              title="Ktor Client"
              description="Kotlin-first HTTP client with co-routines support"
              badge="Network"
              checked={options.includeKtor}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="Decompose"
              title="Decompose"
              description="Component-based navigation for Compose"
              badge="Navigation"
              checked={options.includeDecompose}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="Precompose"
              title="PreCompose"
              description="Navigation, ViewModel and DI for Compose"
              badge="Architecture"
              checked={options.includePrecompose}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="Sentry"
              title="Sentry"
              description="Error tracking and performance monitoring"
              badge="Analytics"
              checked={options.includeSentry}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="Markdown"
              title="Markdown Renderer"
              description="Display and render Markdown content"
              badge="UI"
              checked={options.includeMarkdown}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="ImageLoader"
              title="Image Loading"
              description="Efficient image loading and caching library"
              badge="Media"
              checked={options.includeImageLoader}
              onChange={handleDependencyChange}
            />

            <DependencyCard
              id="KotlinxDatetime"
              title="kotlinx.datetime"
              description="Kotlin multiplatform date/time library"
              badge="Utility"
              checked={options.includeKotlinxDatetime}
              onChange={handleDependencyChange}
            />
          </div>
        </Card>

        <Card className="p-6 shadow-sm hover:shadow-md hover:-translate-y-1 transition-all h-[600px] overflow-hidden">
          <h2 className="text-xl font-semibold text-primary mb-6 pb-2 border-b border-border relative after:content-[''] after:absolute after:bottom-[-2px] after:left-0 after:w-20 after:h-0.5 after:bg-accent hover:after:w-30 after:transition-all">
            Preview
          </h2>

          <Tabs defaultValue="gradle" className="h-[calc(100%-70px)]">
            <TabsList className="mb-4 w-full justify-start overflow-x-auto">
              <TabsTrigger value="gradle">build.gradle.kts</TabsTrigger>
              <TabsTrigger value="settings">settings.gradle.kts</TabsTrigger>
              <TabsTrigger value="versions">libs.versions.toml</TabsTrigger>
              <TabsTrigger value="main">Main.kt</TabsTrigger>
              <TabsTrigger value="readme">README.md</TabsTrigger>
            </TabsList>

            <div className="h-[calc(100%-48px)] overflow-hidden">
              <TabsContent
                value="gradle"
                className="h-full m-0 p-0 data-[state=active]:block"
              >
                <CodePreview code={gradlePreview} language="kotlin" />
              </TabsContent>

              <TabsContent
                value="settings"
                className="h-full m-0 p-0 data-[state=active]:block"
              >
                <CodePreview code={settingsPreview} language="kotlin" />
              </TabsContent>

              <TabsContent
                value="versions"
                className="h-full m-0 p-0 data-[state=active]:block"
              >
                <CodePreview code={versionsPreview} language="toml" />
              </TabsContent>

              <TabsContent
                value="main"
                className="h-full m-0 p-0 data-[state=active]:block"
              >
                <CodePreview code={mainPreview} language="kotlin" />
              </TabsContent>

              <TabsContent
                value="readme"
                className="h-full m-0 p-0 data-[state=active]:block"
              >
                <MarkdownPreview markdown={readmePreview} />
              </TabsContent>
            </div>
          </Tabs>
        </Card>
      </div>

      <WarningSection />

      <div className="flex justify-center mt-12">
        <Button
          type="submit"
          className={`px-10 py-6 text-lg font-semibold bg-gradient-to-r from-gray-800 to-black dark:from-gray-200 dark:to-white dark:text-black hover:-translate-y-1 transition-all animate-glow ${
            !isValid ? "opacity-50 cursor-not-allowed" : ""
          }`}
          disabled={!isValid}
        >
          Generate Project
        </Button>
      </div>
    </form>
  );
}
