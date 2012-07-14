package misc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ExamStack {

	private LinkedList<Exam> stack;
	
	private Boolean tailing = false;
	
	private Condition cond = new ReentrantLock().newCondition();
	
	public ExamStack() {
		stack = new LinkedList<Exam>();
	}
	
	public ExamStack(Collection<Exam> exams) {
		stack = new LinkedList<Exam>();
		stack.addAll(exams);
	}
	
	public void enqueue(Exam e) {
		stack.addLast(e);
		notify();
	}
	
	public Exam dequeue() {
		synchronized (tailing) {
			while (tailing) {
				try {
					cond.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return stack.pollFirst();
	}
	
	public LinkedList<Exam> tail() {
		LinkedList<Exam>  result = new LinkedList<Exam>();
		synchronized (tailing) {
			tailing = true;
			if (stack.size() > 1) {
				result.addAll(stack);
				result.remove(stack.getFirst());
				stack.removeAll(result);
			}
			tailing = false;
			cond.signal();
		}
		return result;
	}
	
	public int size() {
		return stack.size();
	}
//	private Lock lock;
//	private Condition empty;
//
//	public ExamStack(List<Exam> exams) {
//		lock = new ReentrantLock(true);
//		empty = lock.newCondition();
//		if (exams != null) {
//			for (Exam exam : exams) {
//				add(exam);
//			}
//		}
//	}
//	
//
//	public synchronized void putLast(Exam exam) throws InterruptedException {
//		addLast(exam);
////		notifyAll();
//		empty.signalAll();
//	}
//
//	public synchronized Exam pullFirst() throws InterruptedException {
////		lock.lock();
//		while (isEmpty()) {
//			empty.await();
////			empty.
////			wait();
//		}
//		return removeFirst();
//	}
}
