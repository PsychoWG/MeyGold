package misc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExamStack {

	//examstack
	protected LinkedList<Exam> stack;

	//lock for examstack
	private Lock lock = new ReentrantLock();
	//condition stack empty
	private Condition empty = lock.newCondition();

	/**
	 * creates a new empty exam stack 
	 */
	public ExamStack() {
		stack = new LinkedList<Exam>();
	}

	/**
	 * creates a new exam stack with the given exams
	 * @param exams the exams to add to the stack
	 */
	public ExamStack(Collection<Exam> exams) {
		stack = new LinkedList<Exam>();
		stack.addAll(exams);
	}

	/**
	 * adds an element to the top of the stack
	 * 
	 * @param e the element to add
	 */
	public void enqueue(Exam e) {
		try {
			lock.lock();
			stack.addLast(e);
			//signal: stack not empty anymore
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
			ex = stack.removeFirst();
		} finally {
			lock.unlock();
		}
			return ex;
	}

	public List<Exam> tail() {
		try {
			lock.lock();
			if (stack.size() > 1) {
				return stack.subList(1, stack.size() - 1);
			} else {
				return null;
			}
		} finally {
			lock.unlock();
		}
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
