import type React from "react";
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { ThemeProvider } from "@/components/theme-provider";
import { Analytics } from "@vercel/analytics/react";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  metadataBase: new URL("https://composefordesktop.vercel.app"),
  title: "Compose for Desktop Wizard",
  authors: [{ name: "Zahid Khalilov", url: "https://github.com/zahid4kh" }],
  publisher: "Zahid Khalilov",
  description:
    "Generate a production-ready Kotlin Compose for Desktop project in seconds. Features instant setup, custom configuration, interactive dependency selection, live preview, and cross-platform support.",
  keywords: [
    "Compose for Desktop",
    "Kotlin",
    "Project Generator",
    "Desktop Development",
    "Cross-Platform",
    "Material 3",
    "Gradle",
    "File Chooser",
    "Development Tools",
    "UI Framework",
    "Build Configuration",
  ],
  verification: {
    google: "9441a938179c7cbb",
  },
  category: "Development Tools",
  creator: "Zahid Khalilov",
  openGraph: {
    type: "website",
    locale: "en_US",
    title: "Compose for Desktop - Project Generator",
    description:
      "Generate a production-ready Kotlin Compose for Desktop project in seconds. Features instant setup, custom configuration, interactive dependency selection, live preview, and cross-platform support.",
    siteName: "Compose for Desktop Wizard",
    images: [
      {
        url: "/og-image.png",
        width: 1200,
        height: 630,
        alt: "Compose for Desktop Project Generator by zahid4kh or @helloffaday",
      },
    ],
  },
  twitter: {
    card: "summary_large_image",
    title: "Compose for Desktop - Project Generator",
    description:
      "Generate a production-ready Kotlin Compose for Desktop project with instant setup, custom configuration, and live preview.",
    images: ["/og-image.png"],
    creator: "@helloffaday",
  },
  other: {
    "application-name": "Compose for Desktop Wizard",
    author: "Zahid Khalilov",
    owner: "Zahid Khalilov",
    designer: "Zahid Khalilov",
    developer: "Zahid Khalilov",
    copyright: "© 2025 Zahid Khalilov",
    robots: "index,follow",
    googlebot: "index,follow",
    "application-platform": "Cross-Platform",
    "supported-platforms": "Windows, macOS, Linux",
    features:
      "Instant Project Setup, File Chooser, Custom Configuration, Interactive Dependency Selection, Live Code Preview, Material 3 theming, Dark Mode, Production Ready, Cross-Platform",
  },
  icons: {
    icon: [
      { url: "/icon.png", type: "image/png" },
      { url: "/icon.svg", type: "image/svg+xml" },
      { url: "/favicon.ico", type: "image/x-icon" },
    ],
    apple: { url: "/icon.png", type: "image/png" },
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      "max-video-preview": -1,
      "max-image-preview": "large",
      "max-snippet": -1,
    },
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <meta
          name="google-site-verification"
          content="p4B-z5A5VlfIlnjs6UgUubZHR_SrXcChBB_XjSYuoaM"
        />
      </head>
      <body className={inter.className}>
        <script
          type="application/ld+json"
          dangerouslySetInnerHTML={{
            __html: JSON.stringify({
              "@context": "https://schema.org",
              "@type": "WebApplication",
              name: "Compose for Desktop Wizard",
              applicationCategory: "DeveloperApplication",
              operatingSystem: "Windows, macOS, Linux",
              description:
                "Generate a production-ready Kotlin Compose for Desktop project in seconds. Features instant setup, custom configuration, interactive dependency selection, live preview, and cross-platform support.",
              url: "https://composefordesktop.vercel.app",
              author: {
                "@type": "Person",
                name: "Zahid Khalilov",
                url: "https://github.com/zahid4kh",
              },
              creator: {
                "@type": "Person",
                name: "Zahid Khalilov",
                url: "https://github.com/zahid4kh",
              },
              publisher: {
                "@type": "Person",
                name: "Zahid Khalilov",
              },
              offers: {
                "@type": "Offer",
                price: "0",
                priceCurrency: "USD",
              },
              dateCreated: "2025-01-01",
              dateModified: "2025-01-01",
              inLanguage: "en",
              isAccessibleForFree: true,
            }),
          }}
        />
        <ThemeProvider
          attribute="class"
          defaultTheme="dark"
          enableSystem
          disableTransitionOnChange={false}
        >
          {children}
        </ThemeProvider>
        <Analytics />
      </body>
    </html>
  );
}
