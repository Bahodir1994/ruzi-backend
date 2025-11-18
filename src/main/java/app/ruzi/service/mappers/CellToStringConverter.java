package app.ruzi.service.mappers;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.CellData;

public class CellToStringConverter implements Converter<String> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.EMPTY;
    }

    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        CellData<?> cellData = context.getReadCellData();

        if (cellData == null || cellData.getType() == null) {
            return null;
        }

        return switch (cellData.getType()) {
            case STRING, DIRECT_STRING, RICH_TEXT_STRING -> cellData.getStringValue();
            case NUMBER -> String.valueOf(cellData.getNumberValue());
            case BOOLEAN -> String.valueOf(cellData.getBooleanValue());
            case DATE -> cellData.getData().toString();
            default -> null;
        };
    }
}
