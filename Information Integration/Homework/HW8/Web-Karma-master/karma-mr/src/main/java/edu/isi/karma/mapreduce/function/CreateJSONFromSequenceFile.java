package edu.isi.karma.mapreduce.function;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import edu.isi.karma.rdf.CommandLineArgumentParser;

public class CreateJSONFromSequenceFile {
	static String filePath = null;
	static String outputPath = null;
	public static void main(String[] args) throws IOException {
		Options options = createCommandLineOptions();
		CommandLine cl = CommandLineArgumentParser.parse(args, options, CreateJSONFromSequenceFile.class.getSimpleName());
		if(cl == null)
		{
			return;
		}
		filePath = (String) cl.getOptionValue("filepath");
		outputPath = filePath;
		if (cl.hasOption("outputpath")) {
			outputPath = (String) cl.getOptionValue("outputpath");
		}
		FileSystem hdfs = FileSystem.get(new Configuration());
		RemoteIterator<LocatedFileStatus> itr = hdfs.listFiles(new Path(filePath), true);
		while (itr.hasNext()) {
			LocatedFileStatus status = itr.next();
			String fileName = status.getPath().getName();
			if (status.getLen() > 0) {
				String outputFileName = outputPath + File.separator + fileName;// + ".json";
				List<FSDataOutputStream> streams = new LinkedList<FSDataOutputStream>();
				if(cl.hasOption("splits"))
				{
					Integer splits = Integer.parseInt((String) cl.getOptionValue("splits"));
					for(int i = 0; i < splits; i ++)
					{
						streams.add(hdfs.create(new Path(outputFileName+"."+i + ".json")));
					}
				}
				else
				{
					streams.add(hdfs.create(new Path(outputFileName+ ".json")));
				}
				createJSONFromSequenceFileFrom(status.getPath(), streams);
			}
		}
	}

	public static void createJSONFromSequenceFileFrom(Path input, List<FSDataOutputStream> streams) throws IOException {
		Path inputPath = input;
		Configuration conf = new Configuration();
		List<PrintWriter> fws = new LinkedList<PrintWriter>();
		for(FSDataOutputStream stream : streams)
		{
			PrintWriter fw = new PrintWriter(stream);
			fws.add(fw);
			fw.write("[\n");
		}
		SequenceFile.Reader reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(inputPath));
		Text key = new Text();
		Text val = new Text();
		
		int writtenTo = 0;
		Iterator<PrintWriter> pwIterator = fws.iterator();
		while(reader.next(key, val))
		{
			if(!pwIterator.hasNext())
			{
				pwIterator = fws.iterator();
			}
			PrintWriter fw = pwIterator.next();
			if(writtenTo < fws.size())
			{
				writtenTo++;
			}
			else
			{
				fw.write(",\n");
			}
			fw.write(val.toString());
		}
		for(PrintWriter fw : fws)
		{
			fw.write("\n]\n");
			fw.close();
		}
		reader.close();
	}
	
	private static Options createCommandLineOptions() {
		Options options = new Options();
				options.addOption(new Option("filepath", "filepath", true, "location of the input file directory"));
				options.addOption(new Option("outputpath", "outputpath", true, "location of output file directory"));
				options.addOption(new Option("splits", "splits", true, "number of splits per file"));
				options.addOption(new Option("help", "help", false, "print this message"));

		return options;
	}

}
