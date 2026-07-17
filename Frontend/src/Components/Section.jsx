import { useState } from "react";
import "./Section.css";
import { useNavigate } from "react-router-dom";

function Section() {
  const [file, setFile] = useState(null);
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  // file select
  const handleFile = (selectedFile) => {
    setFile(selectedFile);
  };

  const handleDrop = (e) => {
    e.preventDefault();

    if (e.dataTransfer.files.length > 0) {
      handleFile(e.dataTransfer.files[0]);
    }
  };

  // =========================
  // 1. ONLY RESUME ANALYSIS
  // =========================
  const submitResume = async () => {
    if (!file) {
      alert("Please select a PDF file");
      return;
    }

    setLoading(true);
    setResult("");

    try {
      const formData = new FormData();
      formData.append("file", file);

      const response = await fetch(
        "https://faceyourinterview-9.onrender.com/api/pdf/analyze",
        {
          method: "POST",
          body: formData,
        }
      );

      if (!response.ok) {
        throw new Error("Resume analysis failed");
      }

      const data = await response.json();

      setResult(data.result);
    } catch (error) {
      console.error(error);
      setResult("Failed to analyze resume.");
    } finally {
      setLoading(false);
    }
  };

  // =========================
  // 2. LIVE INTERVIEW START
  // =========================
  const startInterview = async () => {
    if (!file) {
      alert("Please upload your resume first.");
      return;
    }

    setLoading(true);

    try {
      // mic permission
      await navigator.mediaDevices.getUserMedia({ audio: true });

      // if resume not analyzed yet → analyze first
      let resumeText = result;

      if (!resumeText) {
        const formData = new FormData();
        formData.append("file", file);

        const response = await fetch(
          "https://faceyourinterview-9.onrender.com/api/pdf/analyze",
          {
            method: "POST",
            body: formData,
          }
        );

        if (!response.ok) {
          throw new Error("Resume analysis failed");
        }

        const data = await response.json();
        resumeText = data.result;
        setResult(resumeText);
      }

      // create interview session
      const sessionRes = await fetch(
        "https://faceyourinterview-9.onrender.com/api/interview/session",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ resumeText }),
        }
      );

      if (!sessionRes.ok) {
        throw new Error("Session creation failed");
      }

      const sessionId = await sessionRes.text();

      // navigate to interview page
      navigate("/live-interview", {
        state: { sessionId },
      });
    } catch (error) {
      console.error(error);
      alert("Unable to start interview");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="section-container">
      {/* UPLOAD BOX */}
      <div
        className="upload-box"
        onDragOver={(e) => e.preventDefault()}
        onDrop={handleDrop}
      >
        <p>📄 Drag & Drop Your Resume Here</p>
        <p>or</p>

        <input
          className="file-input"
          type="file"
          accept=".pdf"
          onChange={(e) => handleFile(e.target.files[0])}
        />

        {file && (
          <p className="file-name">
            Selected File: <strong>{file.name}</strong>
          </p>
        )}
      </div>

      {/* BUTTONS */}
      <div className="button-group">
        <button
          className="submit-btn"
          onClick={submitResume}
          disabled={loading}
        >
          {loading ? "Analyzing..." : "Submit Resume"}
        </button>

        <button
          className="live-btn"
          onClick={startInterview}
          disabled={loading || !file}
        >
          🎤 Live Interview
        </button>
      </div>

      {/* RESULT */}
      {result && (
        <div className="result">
          <h3>Resume Analysis</h3>
          <pre className="analysis-text">{result}</pre>
        </div>
      )}
    </div>
  );
}

export default Section;