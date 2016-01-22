package com.textspeed.app.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdvancedAdapter<A,B extends AdapterFormat<A>> extends BaseAdapter {
	
	protected Activity activity;
	protected int viewId;
	
	protected List<A> list;
	protected B format;
	
	protected List<Integer> filtered;
	protected Comparator<A> comparator;
	private final Filter<A> clearFilter = new Filter<A>() {
		@Override
		public boolean isOk(A item) {
			return true;
		}			
	};
	
	protected Filter<A> filter = clearFilter;

	public AdvancedAdapter(Activity context, int viewId, List<A> list, B format, Filter<A> filter, Comparator<A> comparator) {
		super();
		this.activity = context;
		this.viewId = viewId;
		this.list = list;
		this.format = format;
		this.filter = filter;
		this.comparator = comparator;
		sortAndFilter();
	}

	public List<A> getList() {
		return list;
	}
	
	/**
	 * Очистка список от всех элементов
	 */
	public void clear() {
		setList(new ArrayList<A>());
	}

	public void setList(List<A> list) {
		this.list = list;
		sortAndFilter();
		notifyDataSetChanged();
	}

	public void setAdapterFormat(B adapter) {
		format = adapter;
		notifyDataSetChanged();
	}

	public void updateList(List<A> list) {
		
		ArrayList<A> toAdd = new ArrayList<A> (list);
    	ArrayList<A> toRemove = new ArrayList<A> (this.list);
    	
    	toAdd.removeAll(this.list);
    	toRemove.removeAll(list);
    	
    	for(A i : toAdd) {
    		this.list.add(i);
    	}
    	
    	for(A i : toRemove) {
    		this.list.remove(i);
    	}
    	
    	if(toAdd.size()>0 || toRemove.size()>0) {
			sortAndFilter();
			notifyDataSetChanged();
    	}
	}
	
	private List<Integer> getFiltered(List<A> list, Filter<A> filter) {
		List<Integer> filtered = new ArrayList<Integer>();
		int l = list==null? 0 : list.size();
		for(int i=0; i<l; i++) {
			if(filter.isOk(list.get(i))) {
				filtered.add(i);
			}
		}
		return filtered;
	}

	public void filter() {
		if(filter==null) {
			filtered = getFiltered(list, clearFilter);
		} else {
			filtered = getFiltered(list, filter);
		}
		notifyDataSetChanged();
	}

	public void filter(Filter<A> filter) {
		this.filter = filter;
		filter();
	}

	public void sortAndFilter() {
		sort();
		filter();
	}

	public void sort(Comparator<A> comparator) {
		this.comparator = comparator;
		sort();
	}

	public void sort() {
		if(list!=null && comparator!=null) {
			Collections.sort(list, comparator);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
 		
		if(view==null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			view = inflater.inflate(viewId, parent, false);
		}
		A data = getItem(position);		
		format.setData(data, view);
		
		return view;
	}

	@Override
	public int getCount() {
		return filtered==null? 0 : filtered.size();
	}

	@Override
	public A getItem(int position) {
		return list.get(filtered.get(position));
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setItem(int i, A item) {
		list.add(i, item);
		sortAndFilter();
	}

	public void addNewItem(A item) {
		list.add(item);
		sortAndFilter();
	}

	public void addNewItems(List<A> items) {
		list.addAll(items);
		sortAndFilter();
	}

	public void removeItem(A item) {
		boolean removed = list.remove(item);
		if(removed) {
			sortAndFilter();
			notifyDataSetChanged();
		}
	}

	public int findItemIndex(Filter<A> description) {
        int id = -1;
        int l = list.size();
        for (int i = 0; i < l; i++) {
            if (description.isOk(list.get(i))) {
                id = i;
                break;
            }
        }
        return id;
    }

}
