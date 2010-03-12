package ca.etsmtl.logti.log792.scam.corpus;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.etsmtl.logti.log792.scam.descriptor.label.AudioLabler;
import ca.etsmtl.logti.log792.scam.descriptor.label.Transcription;
import ca.etsmtl.logti.log792.scam.exception.ScamException;

public class Corpus {
    
    private final static Logger logger = Logger.getLogger(Corpus.class);
    
    private final String XML_CORPUS_GENRE = "genre";
    private final String XML_CORPUS_GENRE_SUBGENRE = "sub-genre";
    private final String XML_CORPUS_GENRE_A_LABEL = "label";
    private final String XML_CORPUS_TRACK = "track";
    private final String XML_CORPUS_TRACK_A_FILE = "file";
    
    private final String CORPUS_FILENAME = "corpus.xml";
    
    private String path = null;
    private String name = null;
    private Transcription labels = null;
    private Set<Track> tracks = new TreeSet<Track>();
    private String parent = null;
    
    public Corpus(String path, String name) {
        this.path = path;
        this.name = name;
       
        load();
    }
    
    private Corpus(String path, String name, boolean reload) {
        this.path = path;
        this.name = name;
       
        if (reload) {
            load();
        }
    }
    
    public void loadTranscriptions() {
        AudioLabler audioLabler = new AudioLabler(this);
        this.labels = audioLabler.build();
    }
    
    private void load() {
        try {
            File file = new File(path, CORPUS_FILENAME);
            logger.debug(file.getAbsolutePath());
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList genreList = doc.getElementsByTagName(XML_CORPUS_GENRE);
            for (int i = 0; i < genreList.getLength(); i++) {
                Node genreNode = genreList.item(i);
                if (genreNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element genreElement = (Element) genreNode;
                    
                    NodeList trackList = genreElement.getElementsByTagName(XML_CORPUS_TRACK);
                    for (int j = 0; j < trackList.getLength(); j++) {
                        Node trackNode = trackList.item(j);
                        Genre genre = new Genre(genreElement.getAttribute(XML_CORPUS_GENRE_A_LABEL));
                        if (trackNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element trackElement = (Element) trackNode;
                            Node parent = trackElement.getParentNode();
                            while (true) {
                                logger.debug(parent.getNodeName());
                                if (parent.getNodeName().equals(XML_CORPUS_GENRE_SUBGENRE)) {
                                    Element subGenreElement = (Element) parent;
                                    genre.addSubGenre(new Genre(subGenreElement.getAttribute(XML_CORPUS_GENRE_A_LABEL)));
                                    parent = parent.getParentNode();
                                } else if (parent.getNodeName().equals(XML_CORPUS_GENRE)) {
                                    break;
                                }
                            }
                            Track track = new Track(new File(path, trackElement.getAttribute(XML_CORPUS_TRACK_A_FILE)), genre);
                            addTrack(track);
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            logger.error("Error while loading corpus [" + path + "]", ex);
        }
    }
    
    private void addTrack(Track track) {
        tracks.add(track);
    }
    
    private void setParent(String parent) {
        this.parent = parent;
    }
    
    public Corpus getCorpusByGenre(Genre genre) {
        Corpus corpus = new Corpus(this.getPath(), this.getName() + "." + genre.getLabel(), false);
        Iterator<Track> it = tracks.iterator();
        while (it.hasNext()) {
            Track track = it.next();
            if (track.getAcutalGenre().getLabel().equalsIgnoreCase(genre.getLabel())) {
                try {
                    if (track.getAcutalGenre().getSubGenre().size() > 0) {
                        Genre subGenre = track.getAcutalGenre().getSubGenre().get(0);
                        Track subCorpusTrack = new Track(track.getOriginalFile(), subGenre);
                        corpus.addTrack(subCorpusTrack);
                    }
                } catch (ScamException ex) {
                    logger.error("Error rebuilding track for sub-corpus");
                }
            }
        }
        corpus.setParent(genre.getLabel());
        logger.debug(corpus.tracks.size() + " tracks in sub-corpus");
        return corpus;
    }
    
    public Set<Track> getTracks() {
        return tracks;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getName() {
        return name;
    }
    
    public String getParent() {
        return parent;
    }
    
    public Transcription getLabels() {
        return labels;
    }
}
