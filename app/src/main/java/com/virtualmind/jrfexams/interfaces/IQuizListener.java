package com.virtualmind.jrfexams.interfaces;

public interface IQuizListener {
   void onAnswerAttempted();
   void onCorrectAnswer();
   void onInCorrectAnswer();
}
