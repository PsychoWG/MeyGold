package misc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExamStack {

	private LinkedList<Exam> stack;

	private Lock lock = new ReentrantLock();
	private Condition cond = lock.newCondition();
	private Condition wait = lock.newCondition();

	public ExamStack() {
		stack = new LinkedList<Exam>();
	}

	public ExamStack(Collection<Exam> exams) {
		stack = new LinkedList<Exam>();
		stack.addAll(exams);
	}

	public void enqueue(Exam e) {
		try {
			lock.lock();
			stack.addLast(e);
			wait.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public Exam dequeue() {
		try {
			lock.lock();
			while (stack.isEmpty()) {
				wait.await();
			}
			Exam ex = stack.removeFirst();
			return ex;
		} catch (InterruptedException e) {
			return null;
		} finally {
			lock.unlock();
		}
	}

	public LinkedList<Exam> tail() {
		return null;
	}

	public int size() {
		return stack.size();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}
}
