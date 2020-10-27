package com.skteam.jrfexams.interfaces;

public interface IQuizListener {
   void onAnswerAttempted();
   void onCorrectAnswer();
   void onInCorrectAnswer();
}
