package com.textspeed.app.ui;

/**
 * Интерфейс - фильтр. ПРименяется в основном для фильтрации списков
 *
 * @param <A>
 */
public interface Filter<A> {
	public boolean isOk(A item);
}
