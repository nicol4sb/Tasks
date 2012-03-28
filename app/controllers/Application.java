package controllers;

import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.EntityNotFoundException;

import models.Task;
import play.data.validation.Valid;
import play.modules.objectify.Datastore;
import play.modules.objectify.ObjectifyService;
import play.mvc.Controller;

public class Application extends Controller {
	
	public static void index() {
		Collection<Task> tasks = Task.findAll();
		
		int numberOfOpenTasks = 0;
		double monthlyBudget = 0;
		for (Task task : tasks) {
			if(!task.done){
				numberOfOpenTasks++;
			}
			monthlyBudget+=task.budget;			
		}
		render(tasks, numberOfOpenTasks, monthlyBudget);
	}

	public static void add(final @Valid Task task) {
		task.save();
		index();
	}
	
	public static void delete(final @Valid Task task) {
		task.delete();
		index();
	}

	public static void toggleStar(final Long id) throws EntityNotFoundException{
		Task task = Task.findById(id);
		task.starred = !task.starred;
		Datastore.put(task);
		index();
	}
	
	public static void toggleDone(final Long id) throws EntityNotFoundException{
		Task task = Task.findById(id);
		task.done = !task.done;
		Datastore.put(task);
		index();
	}

	public static void getTaskContent(Long id) throws EntityNotFoundException {
		Task task = Task.findById(id);
		renderHtml(task.content);
	}
	
	public static void getTaskBudget(Long id) throws EntityNotFoundException {
		Task task = Task.findById(id);
		renderText(task.budget);
	}
	
	public static void updateTaskContent(final Long id, String value) throws EntityNotFoundException {
		Task task = Task.findById(id);
		task.content = value;
		task.save();
		// return the markuped content.
		// Yes, the "real" content will always be fetched from the server before
		// edition.
		renderHtml(play.templates.JavaExtensions.nl2br(task.content));
	}
	
	public static void updateBudget(final Long id, String value) throws EntityNotFoundException {
		Task task = Task.findById(id);
		task.budget = Double.valueOf(value);
		task.save();
		renderText(task.budget);
	}
	
	public static void getCompleteBudget(){
		Collection<Task> tasks = Task.findAll();
		
		double completeBudget = 0;
		for (Task task : tasks) {
			completeBudget+=task.budget;			
		}
		renderText(completeBudget);
	}
	
}