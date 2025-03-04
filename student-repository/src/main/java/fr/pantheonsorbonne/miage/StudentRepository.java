
package fr.pantheonsorbonne.miage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class StudentRepository implements Iterable<Student> {

	private String db;
	private Iterator<Student> currentIterator;
	

	private StudentRepository(String db) {
		this.db = db;
	}

	public static StudentRepository withDB(String db) {
		if (!Files.exists(Paths.get(db))) {
			throw new RuntimeException("failed to find" + Paths.get(db).toAbsolutePath().toString());
		}
		return new StudentRepository(db);
	}

	public StudentRepository add(Student s) {
		Iterator<Student> previousContent = StudentRepository.withDB(this.db).iterator();
		try (FileWriter writer = new FileWriter(this.db)) {
			CSVPrinter csvFilePrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

			List<?> lst = StreamSupport.stream(this.spliterator(), false)
					.map((student -> Arrays.asList(student.getId(), student.getName(), student.getTitle())))
					.collect(Collectors.toList());
			for (Object o : lst) {
				csvFilePrinter.printRecord(o);
			}
			csvFilePrinter.close();

		} catch (IOException e) {
			throw new UnsupportedOperationException("failed to update db file");
		}
		return this;

	}

	@Override
	public java.util.Iterator<Student> iterator() {
  		java.util.Iterator<Student> currentIterator = null;

		try (FileReader reader = new FileReader(this.db)) {

			CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT);
			this.currentIterator = parser.getRecords().stream()
			.map((reccord) -> new Student(Integer.parseInt(reccord.get(2)), reccord.get(0), reccord.get(1), reccord.get(3)))					.map(c -> (Student) c).iterator();
			return this.currentIterator;

		} catch (IOException e) {
			Logger.getGlobal().info("IO PB" + e.getMessage());
			return Collections.emptyListIterator();
		}
	}

}
