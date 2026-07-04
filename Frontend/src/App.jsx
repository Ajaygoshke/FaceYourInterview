import { Routes, Route } from "react-router-dom";
import Navbar from "./Components/Navbar";
import Section from "./Components/Section";
import Resume from "./ResumeComponent/ResumeImages";
import CareerBoard from "./CareerOption/CareerBoard";
import LiveInterview from "./InterviewComponent/LiveInterview";

function App() {
  return (
    <>
      <Navbar />

      <Routes>
        <Route path="/home" element={<Section />} />
        <Route path="/career" element={<CareerBoard />} />
        <Route path="/resume" element={<Resume />} />
        <Route path="/jobs" element={<h1>Jobs Page</h1>} />

        {/* ✅ ADD THIS */}
        <Route path="/live-interview" element={<LiveInterview />} />
      </Routes>
    </>
  );
}

export default App;