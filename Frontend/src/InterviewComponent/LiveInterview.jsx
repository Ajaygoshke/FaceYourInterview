import { useEffect, useState, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./LiveInterview.css";

function LiveInterview() {
    const location = useLocation();
    const navigate = useNavigate();
    const sessionIdFromNav = location.state?.sessionId;

    const [sessionId, setSessionId] = useState(null);
    const [status, setStatus] = useState("Setup your custom profile parameters");
    const [questionNo, setQuestionNo] = useState(1);
    const [currentQuestion, setCurrentQuestion] = useState("");
    const [transcript, setTranscript] = useState("");
    const [isListening, setIsListening] = useState(false);
    
    // Custom Interview Configurations
    const [isConfigured, setIsConfigured] = useState(false);
    const [totalQuestions, setTotalQuestions] = useState(5); // Defaults to 5 questions
    const [selectedLanguage, setSelectedLanguage] = useState("");

    // Completion States
    const [isCompleted, setIsCompleted] = useState(false);
    const [finalScore, setFinalScore] = useState("");
    const [aiFeedback, setAiFeedback] = useState("");

    const sessionIdRef = useRef(null);
    const recognitionRef = useRef(null);

    const popularLanguages = ["Java", "JavaScript", "Python", "C++", "Go", "TypeScript", "SQL"];

    // INITIALIZATION: Handles generation or capture of the session ID immediately on mount
    useEffect(() => {
        // 1. Attempt to capture from navigation or localStorage
        let targetId = sessionIdFromNav || localStorage.getItem("activeSessionId");
        
        // 2. Bypass resume guard fallback: If no session exists, dynamically generate an ad-hoc session
        if (!targetId) {
            targetId = `session_lang_${Date.now()}`;
            localStorage.setItem("activeSessionId", targetId);
        }
        
        setSessionId(targetId);
        sessionIdRef.current = targetId;
        setStatus("Ready! Pick your target language parameters.");
        
        return () => {
            if (recognitionRef.current) recognitionRef.current.stop();
            window.speechSynthesis.cancel();
        };
    }, [sessionIdFromNav]);

    // TRIGGER DYNAMIC SETUP INITIALIZATION
    const handleStartSetupInterview = async () => {
        const activeId = sessionIdRef.current || sessionIdFromNav || sessionId;

        if (!activeId) {
            alert("Session token could not be initialized. Please refresh the page.");
            return;
        }

        if (!selectedLanguage) {
            alert("Please pick a programming language focus target before continuing.");
            return;
        }

        setIsConfigured(true);
        setStatus("Assembling specialized question matrix...");

        try {
            const response = await fetch(
                `http://localhost:8080/api/interview/start?sessionId=${activeId}`,
                { 
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        totalQuestions: parseInt(totalQuestions, 10),
                        selectedLanguage: selectedLanguage
                    })
                }
            );
            
            if (!response.ok) throw new Error("API Connection broken during setup initialization.");
            const data = await response.json();
            
            if (data && data.question) {
                setCurrentQuestion(data.question);
                speak(data.question);
            }
        } catch (err) {
            console.error(err);
            setStatus("Unable to generate custom context interview questions.");
            setIsConfigured(false); // Drop back to configuration view safely on fallback failures
        }
    };

    const speak = (text) => {
        setStatus("AI is speaking question...");
        setIsListening(false);
        setTranscript(""); // Flush prior context workspace buffers
        window.speechSynthesis.cancel();

        const speech = new SpeechSynthesisUtterance(text);
        speech.lang = "en-US";
        speech.onend = () => startListening();
        speech.onerror = () => startListening();
        window.speechSynthesis.speak(speech);
    };

    const startListening = () => {
        setStatus("Microphone hot. Provide your comprehensive response...");
        setIsListening(true);

        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        if (!SpeechRecognition) {
            setStatus("Speech API missing. Switch to Google Chrome.");
            return;
        }

        const recognition = new SpeechRecognition();
        recognition.lang = "en-US";
        recognition.continuous = true;
        recognition.interimResults = true;

        recognition.onresult = (event) => {
            let finalTranscript = "";
            for (let i = event.resultIndex; i < event.results.length; ++i) {
                if (event.results[i].isFinal) {
                    finalTranscript += event.results[i][0].transcript + " ";
                }
            }
            if (finalTranscript) {
                setTranscript((prev) => prev + finalTranscript);
            }
        };

        recognition.onerror = (event) => {
            if (event.error !== "no-speech") {
                console.error("Speech structural error recorded: ", event.error);
            }
        };

        recognition.onend = () => {
            setIsListening(false);
        };

        recognitionRef.current = recognition;
        recognition.start();
    };

    const handleSubmitAnswer = () => {
        if (recognitionRef.current) {
            recognitionRef.current.stop();
        }
        setIsListening(false);
        sendAnswer(transcript.trim());
    };

    const sendAnswer = async (answerToSend) => {
        const activeId = sessionIdRef.current || sessionIdFromNav || sessionId;

        if (!answerToSend || answerToSend.length < 2) {
            setStatus("Answer detected as blank. Speak into the mic and try again.");
            setTimeout(() => startListening(), 2000);
            return;
        }

        setStatus("Processing criteria evaluation parameters...");
        try {
            const response = await fetch(
                `http://localhost:8080/api/interview/answer?sessionId=${activeId}`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ answer: answerToSend })
                }
            );
            const data = await response.json();

            if (data.question.includes("Interview Completed")) {
                setIsCompleted(true);
                setStatus("Complete!");
                setFinalScore(data.question.match(/Final Score:\s*\d+\/\d+/) ? data.question.match(/Final Score:\s*\d+\/\d+/)[0] : "Completed");
                setAiFeedback(data.question.split("Feedback:")[1] || "Thank you for completing this specialized loop evaluation.");
            } else {
                setQuestionNo((prev) => prev + 1);
                setCurrentQuestion(data.question);
                speak(data.question);
            }
        } catch (err) {
            setStatus("Error running assessment logic execution frameworks.");
        }
    };

    // SETUP RENDER SCREEN (Pre-Interview configuration layout form)
    if (!isConfigured) {
        return (
            <div className="interview-container">
                <div className="interview-box config-card">
                    <h2>Custom Interview Dashboard Configuration</h2>
                    <p className="subtitle-desc">Define your interview boundaries below before initialization:</p>
                    
                    <div className="form-group">
                        <label>Choose a target Programming Language focus:</label>
                        <div className="language-chip-container">
                            {popularLanguages.map((lang) => (
                                <button
                                    key={lang}
                                    type="button"
                                    className={`lang-chip ${selectedLanguage === lang ? "selected-chip" : ""}`}
                                    onClick={() => setSelectedLanguage(lang)}
                                >
                                    {lang}
                                </button>
                            ))}
                        </div>
                    </div>

                    <button className="start-interview-btn" onClick={handleStartSetupInterview}>
                        🚀 Launch Specialized AI Interview Session
                    </button>
                </div>
            </div>
        );
    }

    if (isCompleted) {
        return (
            <div className="interview-container">
                <div className="interview-box completion-box">
                    <h1>🎉 Interview Wrap-Up</h1>
                    <div className="score-badge"><h2>{finalScore}</h2></div>
                    <p className="feedback-text"><strong>Feedback Summary:</strong> {aiFeedback}</p>
                    <button className="dashboard-btn" onClick={() => navigate("/dashboard")}>Return Home</button>
                </div>
            </div>
        );
    }

    // MAIN RUNNING LIVE INTERVIEW VIEW SCREEN
    return (
        <div className="interview-container">
            <div className="interview-box">
                <span className="topic-tag">Target Stack Focus: {selectedLanguage}</span>
                <h2 className="question-no">Question {questionNo} of {totalQuestions}</h2>
                <div className={`status-badge ${isListening ? "active-mic" : ""}`}>{status}</div>
                
                {currentQuestion && <div className="question-display"><p>{currentQuestion}</p></div>}
                
                <div className="transcript-box">
                    <p className="transcript-label">Your Audio Transcript Workspace Preview:</p>
                    <p className="transcript-text">{transcript || "Processing vocal components..."}</p>
                </div>

                {isListening && <button className="submit-answer-btn" onClick={handleSubmitAnswer}>✋ Done Speaking & Submit</button>}
            </div>
        </div>
    );
}

export default LiveInterview;