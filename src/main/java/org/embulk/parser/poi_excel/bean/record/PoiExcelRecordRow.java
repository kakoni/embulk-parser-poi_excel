package org.embulk.parser.poi_excel.bean.record;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.embulk.parser.poi_excel.PoiExcelParserPlugin;
import org.embulk.parser.poi_excel.bean.PoiExcelColumnBean;
import org.embulk.spi.Exec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoiExcelRecordRow extends PoiExcelRecord {
	private static final Logger logger = LoggerFactory.getLogger(PoiExcelRecordRow.class);

	private Iterator<Row> rowIterator;
	private Row currentRow;

	@Override
	protected void initializeLoop(int skipHeaderLines) {
		this.rowIterator = getSheet().iterator();
		this.currentRow = null;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			int rowIndex = row.getRowNum();
			if (rowIndex < skipHeaderLines) {
				if (logger.isDebugEnabled()) {
					logger.debug("row({}) skipped", rowIndex);
				}
				continue;
			}

			this.currentRow = row;
			break;
		}
	}

	@Override
	public boolean exists() {
		return currentRow != null;
	}

	@Override
	public void moveNext() {
		if (rowIterator.hasNext()) {
			this.currentRow = rowIterator.next();
		} else {
			this.currentRow = null;
		}
	}

	@Override
	protected void logStartEnd(String part) {
		assert currentRow != null;
		if (logger.isDebugEnabled()) {
			logger.debug("row({}) {}", currentRow.getRowNum(), part);
		}
	}

	@Override
	public int getRowIndex(PoiExcelColumnBean bean) {
		assert currentRow != null;
		return currentRow.getRowNum();
	}

	@Override
	public int getColumnIndex(PoiExcelColumnBean bean) {
		return bean.getColumnIndex();
	}

	@Override
	public Cell getCell(PoiExcelColumnBean bean) {
		assert currentRow != null;
		int columnIndex = getColumnIndex(bean);
		return currentRow.getCell(columnIndex);
	}
}
