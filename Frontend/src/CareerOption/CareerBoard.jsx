import { useState } from "react";

function JobRoles() {
  const roles = [
    "Java Developer",
    "Frontend Developer",
    "Backend Developer",
    "Full Stack Developer",
    "Data Analyst",
    "DevOps Engineer"
  ];

  const [selectedRole, setSelectedRole] = useState("");

  return (
    <div style={{ padding: "20px" }}>
      <h2>Select Job Role</h2>

      <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
        {roles.map((role, index) => (
          <button
            key={index}
            onClick={() => setSelectedRole(role)}
            style={{
              padding: "10px 15px",
              borderRadius: "8px",
              border: "1px solid #ccc",
              cursor: "pointer",
              backgroundColor: selectedRole === role ? "#4f46e5" : "#fff",
              color: selectedRole === role ? "#fff" : "#000"
            }}
          >
            {role}
          </button>
        ))}
      </div>

      {selectedRole && (
        <p style={{ marginTop: "20px" }}>
          Selected Role: <b>{selectedRole}</b>
        </p>
      )}
    </div>
  );
}

export default JobRoles;