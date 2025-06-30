export default function Footer() {
  return (
    <footer className="py-10 px-4 text-center border-t border-border">
      <p>
        <span className="font-bold text-primary text-lg tracking-tight">
          Compose for Desktop Wizard{" "}
        </span>
        is an open-source project made by{" "}
        <a
          href="https://github.com/zahid4kh"
          target="_blank"
          className="text-primary font-semibold hover:underline hover:text-purple-700 transition-colors"
          rel="noreferrer"
        >
          zahid4kh
        </a>
        . View it on{" "}
        <a
          href="https://github.com/zahid4kh/compose-for-desktop"
          target="_blank"
          className="text-primary font-semibold hover:underline hover:text-purple-700 transition-colors"
          rel="noreferrer"
        >
          GitHub
        </a>
      </p>
    </footer>
  );
}
