package professor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import misc.Exam;
import misc.ExamStack;
import assistant.Assistant;

public class Professor implements Runnable{

	private final ExamStack stackcorrected;
	private final ExamStack stackfinished;
	private Map<Integer, Assistant> assistants;
	
	private Boolean wantShuffle = false;
	private Boolean shuffling = false;
	private Condition alert = new ReentrantLock().newCondition();
	
	
	public Professor(ExamStack corrected, ExamStack finished) {
		this.stackcorrected = corrected;
		this.stackfinished = finished;
	}
	
	public Condition getDatFreakingCondition(Collection<Assistant> ass) {
		assistants = new HashMap<Integer, Assistant>();
		for (Assistant assistant : ass) {
			int i = ass.hashCode();
			int n = (i * (i + 12345));
			int speed = xorShift(n) % 1000000;
			assistants.put(speed, assistant);
		}
		return alert;
	}

	private static int xorShift(int y) {
		y ^= (y << 6);
		y ^= (y >>> 21);
		y ^= (y << 7);

		return Math.abs(y);
	}
	// TODO implement CheckAll for b)
	
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try{
				synchronized (stackcorrected) {
					while(stackcorrected.isEmpty()) {
						alert.await();
					}
					if (!stackcorrected.isEmpty()) {
						finishExam(stackcorrected.pollFirst());
					} else {
						startShuffling();
					}
				}
			} catch (InterruptedException e) {
				
			}
		}
		System.out.println("FEIERABEND!");
	}

	private void startShuffling() {
		// TODO Auto-generated method stub
		
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
