package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.QueryResultIterable;

import play.data.validation.Required;
import play.modules.objectify.Datastore;
import play.modules.objectify.ObjectifyModel;
import play.modules.objectify.ObjectifyService;

public class Task extends ObjectifyModel implements Serializable {

	private static final String TASKS_MAP = "tasksMap";

	@Id
	public Long id;

	public Boolean starred;
	public Boolean done;
	public Double budget;

	@Required
	public String title;
	@Required
	public String content;
	public Date createdAt;
	public Date author;

	public Task() {
		this.createdAt = new Date();
		starred = false;
		done = false;
		budget = 0.0;
	}

	public static Collection<Task> findAll() {

		// create the object, populate it, and update the cache map
		Map tasksMap = new TreeMap<Long, Task>();

		QueryResultIterable<Task> q = Datastore.query(Task.class).order("done")
				.order("-starred").order("title").fetch();
		
		for (Task task : q) {
			tasksMap.put(task.id, task);
		}
		
		return tasksMap.values();
	}

	public static Task findById(Long id) {
		try {
			return Datastore.get(Task.class, id);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void save() {
		// persist the task
		Datastore.put(this);
	}

	public void delete() {
		Datastore.delete(this);
	}

}
