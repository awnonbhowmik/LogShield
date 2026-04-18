// Server Component — the page shell is static; all interactivity lives in UploadCard (Client).
import UploadCard from '@/components/UploadCard';

export default function UploadPage() {
  return (
    <div className="mx-auto max-w-3xl px-4 py-12">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-white">Upload a log file</h1>
        <p className="mt-1 text-sm text-gray-400">
          Accepts <code>.txt</code> and <code>.log</code> files up to 10 MB.
          Sensitive data will be detected and redacted automatically.
        </p>
      </div>
      <UploadCard />
    </div>
  );
}
