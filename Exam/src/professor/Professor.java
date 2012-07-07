package professor;

import assistant.Assistant;
import misc.Exam;
import misc.ExamStack;

public class Professor implements Runnable{

	private final ExamStack stackcorrected;
	private final ExamStack stackfinished;
	private final Assistant[] assistants;
	
	private Boolean wantShuffle = false;
	private Boolean shuffling = false;
	
	
	public Professor(ExamStack corrected, ExamStack finished, Assistant[] assistants) {
		this.stackcorrected = corrected;
		this.stackfinished = finished;
		this.assistants = assistants;
	}
	
	// TODO implement CheckAll for b)
	
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try{
				synchronized (stackcorrected) {
					while(stackcorrected.isEmpty()) {
						stackcorrected.wait();
					}
					finishExam(stackcorrected.pollFirst());
				}
			} catch (InterruptedException e) {
				
			}
		}
		System.out.println("FEIERABEND!");
	}

	private void finishExam(Exam pollFirst) {
		synchronized (pollFirst) {
			pollFirst.finish();
			synchronized (stackfinished) {
				stackfinished.add(pollFirst);
			}
		}
		synchronized (this) {
			synchronized (wantShuffle) {
				wantShuffle = true;
			}
		}
		
	}

}
