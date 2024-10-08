package org.embulk.parser.poi_excel.bean.record;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.embulk.parser.poi_excel.bean.PoiExcelColumnBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoiExcelRecordColumn extends PoiExcelRecord {
	private static final Logger logger = LoggerFactory.getLogger(PoiExcelRecordColumn.class);

	private int maxColumnIndex;
	private int currentColumnIndex;

	@Override
	protected void initializeLoop(int skipHeaderLines) {
		int minColumnIndex = Integer.MAX_VALUE;
		maxColumnIndex = 0;
		Sheet sheet = getSheet();
		for (Row row : sheet) {
			int firstIndex = row.getFirstCellNum();
			if (firstIndex >= 0) {
				minColumnIndex = Math.min(minColumnIndex, firstIndex);
			}
			maxColumnIndex = Math.max(maxColumnIndex, row.getLastCellNum());
		}

		this.currentColumnIndex = maxColumnIndex;
		for (int i = minColumnIndex; i < maxColumnIndex; i++) {
			if (i < skipHeaderLines) {
				if (logger.isDebugEnabled()) {
					logger.debug("column({}) skipped", i);
				}
				continue;
			}

			this.currentColumnIndex = i;
			break;
		}
	}

	@Override
	public boolean exists() {
		return currentColumnIndex < maxColumnIndex;
	}

	@Override
	public void moveNext() {
		currentColumnIndex++;
	}

	@Override
	protected void logStartEnd(String part) {
		if (logger.isDebugEnabled()) {
			logger.debug("column({}) {}", currentColumnIndex, part);
		}
	}

	@Override
	public int getRowIndex(PoiExcelColumnBean bean) {
		return bean.getColumnIndex();
	}

	@Override
	public int getColumnIndex(PoiExcelColumnBean bean) {
		return currentColumnIndex;
	}

	@Override
	public Cell getCell(PoiExcelColumnBean bean) {
		int rowIndex = getRowIndex(bean);
		Row row = getSheet().getRow(rowIndex);
		if (row == null) {
			return null;
		}
		int columnIndex = getColumnIndex(bean);
		return row.getCell(columnIndex);
	}
}
