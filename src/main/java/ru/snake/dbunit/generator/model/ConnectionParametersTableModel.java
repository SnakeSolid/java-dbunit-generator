package ru.snake.dbunit.generator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * Table model with connection parameter list.
 *
 * @author snake
 *
 */
public final class ConnectionParametersTableModel extends AbstractTableModel implements TableModel {

	/**
	 * Map driver name to map with parameter values.
	 */
	private final Map<String, Map<String, String>> savedParameterValues;

	private final List<String> parameterNames;

	private final List<String> parameterValues;

	private String currentDriver;

	/**
	 * Creates new empty parameters model using configuration.
	 */
	public ConnectionParametersTableModel() {
		this.savedParameterValues = new HashMap<String, Map<String, String>>();
		this.parameterNames = new ArrayList<String>();
		this.parameterValues = new ArrayList<String>();
	}

	@Override
	public int getRowCount() {
		return this.parameterNames.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(final int column) {
		if (column == 0) {
			return "Parameter";
		} else if (column == 1) {
			return "Value";
		}

		return "";
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return String.class;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		if (columnIndex == 0) {
			return this.parameterNames.get(rowIndex);
		} else if (columnIndex == 1) {
			return this.parameterValues.get(rowIndex);
		}

		return null;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex == 1;
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		if (columnIndex == 1) {
			String name = this.parameterNames.get(rowIndex);
			String value = String.valueOf(aValue);

			this.parameterValues.set(rowIndex, value);

			if (currentDriver != null) {
				this.savedParameterValues.computeIfAbsent(currentDriver, k -> new HashMap<>()).put(name, value);
			}
		}
	}

	/**
	 * Set new parameter list and fill old available parameter values.
	 *
	 * @param driverName
	 *            current driver name
	 * @param parameters
	 *            parameters
	 */
	public void setParameters(final String driverName, final List<String> parameters) {
		int oldSize = this.parameterNames.size();

		this.currentDriver = driverName;
		this.parameterNames.clear();
		this.parameterNames.addAll(parameters);
		this.parameterValues.clear();

		Map<String, String> driverParameters = this.savedParameterValues
			.getOrDefault(driverName, Collections.emptyMap());

		for (String name : parameters) {
			String value = driverParameters.getOrDefault(name, "");

			this.parameterValues.add(value);
		}

		int newSize = this.parameterNames.size();
		int minSize = Math.min(oldSize, newSize);

		if (minSize > 0) {
			fireTableRowsUpdated(0, minSize - 1);
		}

		if (oldSize < newSize) {
			fireTableRowsInserted(oldSize, newSize - 1);
		}

		if (oldSize > newSize) {
			fireTableRowsDeleted(newSize, oldSize - 1);
		}
	}

	/**
	 * Returns map with parameter name as key and parameters value as value.
	 *
	 * @return map parameters name to value
	 */
	public Map<String, String> getParameterMap() {
		Map<String, String> parameterMap = new HashMap<>();

		for (int index = 0; index < this.parameterNames.size(); index += 1) {
			String name = this.parameterNames.get(index);
			String value = this.parameterValues.get(index);

			parameterMap.put(name, value);
		}

		return parameterMap;
	}

	@Override
	public String toString() {
		return "ConnectionParametersTableModel [savedParameterValues=" + savedParameterValues + ", parameterNames="
				+ parameterNames + ", parameterValues=" + parameterValues + ", currentDriver=" + currentDriver + "]";
	}

}
