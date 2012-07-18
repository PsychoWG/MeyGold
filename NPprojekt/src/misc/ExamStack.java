package misc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import assistant.Assistant;

public class ExamStack {

	//examstack
	protected ConcurrentLinkedQueue<Exam> stack;

	//lock for examstack
	private Lock lock = new ReentrantLock();
	//condition stack empty
	private Condition empty = lock.newCondition();

	/**
	 * Erzeugt einen neuen leeren exam stack
	 */
	public ExamStack() {
		stack = new ConcurrentLinkedQueue<Exam>();
	}

	/**
	 * Erzeugt einen neuen exam stack und füllt ihn
	 * @param exams exams mit der der exam stack befüllt wird
	 */
	public ExamStack(Collection<Exam> exams) {
		stack = new ConcurrentLinkedQueue<Exam>();
		stack.addAll(exams);
	}

	/**
	 * fügt ein element oben auf den stack ein
	 * 
	 * @param e exam das hinzugefügt werden soll
	 */
	public void enqueue(Exam e) {
			stack.add(e);
		try {
			lock.lock();
			//signal: stack not empty anymore
			empty.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * entfernt das erste element aus dem stack und gibt es zurück
	 * @return das erste element des stacks
	 * @throws InterruptedException wenn der aufrufene thread während des wartens unterbrochen wird
	 */
	public Exam dequeue() throws InterruptedException {
		Exam ex = null;
		try {
			lock.lock();
			//wenn kein element im stack ist warten
			while (stack.isEmpty()) {
				empty.await();
			}
			return stack.poll();
		} finally {
			lock.unlock();
		}
	}

	public List<Exam> tail() {
//		try {
//			lock.lock();
			if (stack.size() > 1) {
				List<Exam> result = new LinkedList<Exam>();
				while (stack.size() > 1) {
					lock.lock();
					try {
						Exam ex = stack.poll();
						if (ex != null) {
							result.add(ex);
						}
					} finally {
						lock.unlock();
					}
				}
				return result;
			} else {
				return null;
			}
//		} finally {
//			lock.unlock();
//		}
	}
	/**
	 * 
	 * @return die größe des stacks
	 */
	public int size() {
		return stack.size();
	}
	
	/**
	 * 
	 * @return wahr wenn der stack leer ist ansonsten falsch
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	/**
	 * entfernt das erste element aus dem stack und gibt es zurück
	 * braucht den {@link Assistant} um checked auf false zu setzen
	 * 
	 * @return das erste element des stacks
	 * @throws InterruptedException wenn der aufrufene thread während des wartens unterbrochen wird
	 */
	public Exam dequeue(Assistant assistant) throws InterruptedException {
		Exam ex = null;
		try {
			lock.lock();
			//wenn kein element im stack ist warten
			while (stack.isEmpty()) {
				empty.await();
			}
			assistant.setChecked(false);
			return stack.poll();
		} finally {
			lock.unlock();
		}
	}
}
