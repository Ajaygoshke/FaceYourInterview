package com.FaceYourInterview.Repositry;

import com.FaceYourInterview.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository; 

public interface ResumeRepository extends JpaRepository<Resume, Long>  {

}
