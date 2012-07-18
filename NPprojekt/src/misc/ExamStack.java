package misc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExamStack {

	//examstack
	protected ConcurrentLinkedQueue<Exam> stack;

	//lock for examstack
	private Lock lock = new ReentrantLock();
	//condition stack empty
	private Condition empty = lock.newCondition();

	/**
	 * creates a new empty exam stack 
	 */
	public ExamStack() {
		stack = new ConcurrentLinkedQueue<Exam>();
	}

	/**
	 * creates a new exam stack with the given exams
	 * @param exams the exams to add to the stack
	 */
	public ExamStack(Collection<Exam> exams) {
		stack = new ConcurrentLinkedQueue<Exam>();
		stack.addAll(exams);
	}

	/**
	 * adds an element to the top of the stack
	 * 
	 * @param e the element to add
	 */
	public void enqueue(Exam e) {
			stack.add(e);
			//signal: stack not empty anymore
		try {
			lock.lock();
			empty.signalAll();
		} finally {
			lock.unlock();
		}
	}
	/**
	 * removes and returns the first element of the stack
	 * @return the first element of the stack
	 * @throws InterruptedException if the current thread is interupted while waiting
	 */
	public Exam dequeue() throws InterruptedException {
		Exam ex = null;
		try {
			lock.lock();
			//wait if there is no element in this stack
			while (stack.isEmpty()) {
				empty.await();
			}
			ex = stack.poll();
		} finally {
			lock.unlock();
		}
			return ex;
	}

	public List<Exam> tail() {
//		try {
//			lock.lock();
			if (stack.size() > 1) {
				List<Exam> result = new LinkedList<Exam>();
				while (stack.size() > 1) {
					result.add(stack.poll());
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
	 * @return the size of this stack
	 */
	public int size() {
		return stack.size();
	}
	
	/**
	 * 
	 * @return true if the stack is empty false otherwise
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}
}
