package scouts.cne.pt.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.utils.ValidationUtils;

@SpringComponent
@UIScope
public class SIIEService implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private File file;
	private HashMap<String, Explorador> map = null;
	private EnumMap<SECCAO, List<Explorador>> mapSeccaoElemento = null;

	public SIIEService() {
		super();
		mapSeccaoElemento = new EnumMap<>(SECCAO.class);
		for (SECCAO seccao : SECCAO.values()) {
			mapSeccaoElemento.put(seccao, new ArrayList<>());
		}
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	public void loadExploradoresSIIE() {
		if (map == null) {
			map = new HashMap<>();

			// ClassLoader classLoader = getClass().getClassLoader();
			try (FileInputStream fis = new FileInputStream(file); XSSFWorkbook myWorkBook = new XSSFWorkbook(fis)) {
				// Return first sheet from the XLSX workbook
				XSSFSheet mySheet = myWorkBook.getSheetAt(0);
				// Get iterator to all the rows in current sheet
				Iterator<Row> rowIterator = mySheet.iterator();
				// Traversing over each row of XLSX file
				HashMap<Integer, String> headerRow = new HashMap<>();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String value = cell.getStringCellValue();
					value = value.replace(" ", "");
					value = value.replace("-", "");
					value = value.replace(".", "");
					value = value.toLowerCase();
					value = ValidationUtils.removeAcentos(value);
					if (headerRow.containsValue(value)) {
						if (!headerRow.containsValue(value + "pai")) {
							headerRow.put(cell.getColumnIndex(), value + "pai");
						} else if (!headerRow.containsValue(value + "mae")) {
							headerRow.put(cell.getColumnIndex(), value + "mae");
						} else {
							headerRow.put(cell.getColumnIndex(), value + "encedu");
						}
					} else {
						headerRow.put(cell.getColumnIndex(), value);
					}
				}
				while (rowIterator.hasNext()) {
					row = rowIterator.next();
					Explorador explorador = new Explorador();
					cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_BLANK:
							explorador.getListaAtributos().put(headerRow.get(cell.getColumnIndex()), null);
							break;
						case Cell.CELL_TYPE_STRING:
						case Cell.CELL_TYPE_FORMULA:
							explorador.getListaAtributos().put(headerRow.get(cell.getColumnIndex()),
									cell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							explorador.getListaAtributos().put(headerRow.get(cell.getColumnIndex()),
									cell.getBooleanCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							explorador.getListaAtributos().put(headerRow.get(cell.getColumnIndex()),
									cell.getDateCellValue());
							break;
						default:
							explorador.getListaAtributos().put(headerRow.get(cell.getColumnIndex()), null);
							break;
						}
					}
					if (explorador.isActivo()) {
						map.put(explorador.getNin(), explorador);
						mapSeccaoElemento.get(explorador.getCategoria()).add(explorador);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Getter for mapSeccaoElemento
	 *
	 * @author anco62000465 2018-01-27
	 * @return the mapSeccaoElemento {@link EnumMap<SECCAO,List<Explorador>>}
	 */
	public EnumMap<SECCAO, List<Explorador>> getMapSeccaoElemento() {
		return mapSeccaoElemento;
	}

	private Elemento convert(Explorador explorador) {
		Elemento elemento = new Elemento();
		elemento.setNome(explorador.getNome());
		elemento.setSeccao(explorador.getCategoria());
		elemento.setNin(explorador.getNin());
		elemento.setEmail(explorador.getEmail());
		return elemento;
	}
}
