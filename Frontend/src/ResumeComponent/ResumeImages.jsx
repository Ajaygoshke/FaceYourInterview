import { useEffect, useState } from "react";
import "./ResumeImages.css";

function ResumeImages() {

  const [role, setRole] = useState("");
  const [file, setFile] = useState(null);
  const [resumes, setResumes] = useState([]);

  const API = "http://localhost:8080/api/resumes";

  // 📥 FETCH ALL RESUMES
  const fetchResumes = async () => {
    try {
      const res = await fetch(API);
      const data = await res.json();
      setResumes(data);
    } catch (error) {
      console.error("Fetch error:", error);
    }
  };

  useEffect(() => {
    fetchResumes();
  }, []);

  // 📤 HANDLE FILE CHANGE
  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  // 🚀 UPLOAD RESUME
  const handleUpload = async () => {
    if (!role) {
      alert("Please select role first");
      return;
    }

    if (!file) {
      alert("Please select a PDF file");
      return;
    }

    const formData = new FormData();
    formData.append("title", file.name);
    formData.append("role", role);
    formData.append("file", file);

    try {
      const res = await fetch(`${API}/upload`, {
        method: "POST",
        body: formData,
      });

      if (res.ok) {
        alert("Uploaded successfully!");
        setFile(null);
        fetchResumes(); // refresh list
      } else {
        alert("Upload failed");
      }

    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="container">

      <h1>Resume Upload System</h1>

<p className="note">
  NOTE: <span>These resumes are for reference only. Do not copy them.</span>
</p>
<p className="contribute-resume">
  <strong>Contribute:</strong>
  <span> Upload your resume to help students learn and improve their profiles.</span>
</p>
      {/* ROLE SELECT */}
      <div>
        <label>Select Role:</label>
        <select value={role} onChange={(e) => setRole(e.target.value)}>
          <option value="">-- Select Role --</option>
          <option>Java Developer</option>
          <option>Frontend Developer</option>
          <option>Backend Developer</option>
          <option>Full Stack Developer</option>
          <option>React Developer</option>
          <option>Python Developer</option>
        </select>
      </div>

      {/* FILE INPUT */}
      <div style={{ marginTop: "10px" }}>
        <input
          type="file"
          accept="application/pdf"
          onChange={handleFileChange}
        />
      </div>

      {/* UPLOAD BUTTON */}
      <button onClick={handleUpload} style={{ marginTop: "10px" }}>
        Upload Resume
      </button>

      <hr />

      {/* RESUME LIST */}
      <h2>Uploaded Resumes</h2>

      <div className="grid">
        {resumes.map((r) => (
          <div className="card" key={r.id}>

            <iframe
              src={`http://localhost:8080${r.pdfUrl}`}
              className="pdf"
              title={r.title}
            />

            <h3>{r.title}</h3>
            <p><b>Role:</b> {r.role}</p>

            <div className="buttons">
              <a
                href={`http://localhost:8080${r.pdfUrl}`}
                target="_blank"
                rel="noreferrer"
              >
                View
              </a>
            </div>

          </div>
        ))}
      </div>

    </div>
  );
}

export default ResumeImages;