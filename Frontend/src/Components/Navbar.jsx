import "./Navbar.css";
import { Link } from "react-router-dom";

function Navbar() {
  return (
    <nav className="navbar">
      <h1 className="logo">Face Your Interview</h1>

      <div className="nav-links">
        <Link to="/home">Home</Link>

        {/* FIXED PATH */}
        <Link to="/live-interview">Interview</Link>

        <Link to="/career">CareerBoard</Link>
        <Link to="/resume">Resumes</Link>
        <Link to="/jobs">Jobs</Link>
      </div>
    </nav>
  );
}

export default Navbar;