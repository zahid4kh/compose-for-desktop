export default function GeneratingOverlay() {
  return (
    <div className="fixed inset-0 bg-black/80 backdrop-blur-sm flex justify-center items-center z-50 transition-opacity">
      <div className="bg-card p-10 rounded-xl text-center shadow-lg animate-fadeInUp white-glow">
        <div className="w-12 h-12 border-4 border-muted border-t-primary rounded-full animate-spin mx-auto mb-6"></div>
        <p className="text-lg">Generating your project...</p>
      </div>
    </div>
  )
}
