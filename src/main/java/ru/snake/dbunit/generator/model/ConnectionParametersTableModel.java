package ru.snake.dbunit.generator.model;

import java.util.ArrayList;
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

	private final List<String> parameterNames;

	private final List<String> parameterValues;

	/**
	 * Creates new empty parameters model using configuration.
	 */
	public ConnectionParametersTableModel() {
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
			this.parameterValues.set(rowIndex, String.valueOf(aValue));
		}
	}

	/**
	 * Set new parameter list.
	 *
	 * @param parameters
	 *            parameters
	 */
	public void setParameters(final List<String> parameters) {
		int oldSize = this.parameterNames.size();

		this.parameterNames.clear();
		this.parameterNames.addAll(parameters);
		this.parameterValues.clear();

		for (String name : parameters) {
			this.parameterValues.add("");
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
		return "ConnectionParametersTableModel [parameterNames=" + parameterNames + ", parameterValues="
				+ parameterValues + "]";
	}

}
