package com.jrelay.ui.shared.utils.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.swing.JComboBox;

import com.jrelay.core.models.Environment.Variable;
import com.jrelay.core.models.Environment.VariableType;
import com.jrelay.core.models.request.HttpHeader;
import com.jrelay.core.models.request.Method;
import com.jrelay.core.models.request.QueryParameter;
import com.jrelay.ui.components.shared.KeyValueRow;
import com.jrelay.ui.components.shared.VariableRow;

public class Mapper {

    private Mapper() {
    }

    public static List<QueryParameter> fromRowsQueryParameters(List<KeyValueRow> rows) {
        return rows.stream()
                .map(row -> new QueryParameter(
                        row.getCheck().isSelected(),
                        row.getKeyField().getText().trim(),
                        row.getValueField().getText().trim()))
                .filter(query -> !query.key().isBlank() && !query.value().isBlank())
                .toList();
    }

    public static List<HttpHeader> fromRowsHttpHeaders(List<KeyValueRow> rows) {
        return rows.stream()
                .map(row -> new HttpHeader(
                        row.getCheck().isSelected(),
                        row.getKeyField().getText().trim(),
                        row.getValueField().getText().trim()))
                .filter(header -> !header.key().isBlank() && !header.value().isBlank())
                .toList();
    }

    public static Method fromComboBox(JComboBox<?> cBox) {
        return switch (cBox.getSelectedIndex()) {
            case 0 -> Method.GET;
            case 1 -> Method.POST;
            case 2 -> Method.PUT;
            case 3 -> Method.PATCH;
            case 4 -> Method.DELETE;
            case 5 -> Method.OPTIONS;
            default -> Method.OPTIONS;
        };
    }

    public static List<Variable> fromRowsToVariables(List<VariableRow> variableRowsList) {
        return variableRowsList.stream()
                .map(row -> {
                    String key = row.getVariableTextField().getText().trim();
                    String initialValue = row.getInitialValueTextField().getText().trim();
                    String currentValue = row.getCurrentValueTextField().getText().trim();
                    boolean enabled = row.getCheck().isSelected();
                    VariableType type = (row.getType().getSelectedIndex() == 0)
                            ? VariableType.DEFAULT
                            : VariableType.SECRET;

                    return new Variable(enabled, key, type, initialValue, currentValue);
                })
                .filter(var -> !var.getKey().isEmpty() && !var.getInitialValue().isEmpty())
                .toList();
    }

    public static List<VariableRow> fromVariablesToRows(List<Variable> variables) {
        List<VariableRow> rows = new ArrayList<>();
        for (int i = 0; i < variables.size(); i++) {
            Variable variable = variables.get(i);
            boolean isFirst = (i == 0);
            VariableRow row = new VariableRow(isFirst);
            row.getCheck().setSelected(variable.isEnabled());
            row.getVariableTextField().setText(variable.getKey());
            row.getInitialValueTextField().setText(variable.getInitialValue());
            row.getCurrentValueTextField().setText(variable.getCurrentValue());
            int typeIndex = (variable.getType() == VariableType.DEFAULT) ? 0 : 1;
            row.getType().setSelectedIndex(typeIndex);
            rows.add(row);
        }
        return rows;
    }

    public static <T> List<KeyValueRow> toRowsFromKeyValue(
            List<T> list,
            Function<T, Boolean> checkExtrator,
            Function<T, String> keyExtractor,
            Function<T, String> valueExtractor) {
        return IntStream.range(0, list.size())
                .mapToObj(i -> {
                    T item = list.get(i);
                    boolean isFist = (i == 0);
                    return new KeyValueRow(isFist, checkExtrator.apply(item), keyExtractor.apply(item),
                            valueExtractor.apply(item));
                })
                .toList();
    }

}
