package edu.stanford.smi.protege.query;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class InstallNarrowFrameStore extends ProtegeJob {
  private static final long serialVersionUID = 8982683075005704375L;

  private static final Logger log = Log.getLogger(InstallNarrowFrameStore.class);
  
  public final static String RDF_LABEL = "rdfs:label";
  public final static String RDF_COMMENT = "rdfs:comment";
  
  public InstallNarrowFrameStore(KnowledgeBase kb) {
    super(kb);
  }

  @Override
  public Boolean run() throws ProtegeException {
    DefaultKnowledgeBase kb = (DefaultKnowledgeBase) getKnowledgeBase();
    SimpleFrameStore fs = (SimpleFrameStore) kb.getTerminalFrameStore();
    NarrowFrameStore nfs = fs.getHelper();
    
    if (!alreadyInstalled(nfs)) {
      QueryNarrowFrameStore qnfs = new QueryNarrowFrameStore(kb.getName(), nfs, getSearchableSlots(), getKnowledgeBase());
      fs.setHelper(qnfs);
    }
    return new Boolean(true);
  }
  
  public boolean alreadyInstalled(NarrowFrameStore nfs) {
    do {
      if (nfs instanceof QueryNarrowFrameStore) {
        if (log.isLoggable(Level.FINE)) {
          log.fine("Query Narrow Frame store already found - no install needed");
        }
        return true;
      }
    } while ((nfs = nfs.getDelegate()) != null);
    return false;
  }
  
  @SuppressWarnings("unchecked")
  public Set<Slot> getSearchableSlots() {
    DefaultKnowledgeBase kb = (DefaultKnowledgeBase) getKnowledgeBase();
    Set<Slot> slots = new HashSet<Slot>();
    if (kb instanceof OWLModel) {
      OWLModel owl = (OWLModel) kb;
      slots.addAll(owl.getOWLAnnotationProperties());
      slots.add((Slot) owl.getRDFProperty(RDF_LABEL));
      slots.add((Slot) owl.getRDFProperty(RDF_COMMENT));
      slots.add(kb.getSystemFrames().getNameSlot());
      return slots;
    } else {
      slots.addAll(kb.getSlots());
    }
    return slots;
  }

}
