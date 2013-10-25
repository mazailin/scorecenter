// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.scorecenter.domain;

import com.ruyicai.scorecenter.domain.Tjmsservice;
import com.ruyicai.scorecenter.domain.TjmsservicePK;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Tjmsservice_Roo_Entity {
    
    declare @type: Tjmsservice: @Entity;
    
    declare @type: Tjmsservice: @Table(name = "TJMSSERVICE");
    
    @PersistenceContext
    transient EntityManager Tjmsservice.entityManager;
    
    @EmbeddedId
    private TjmsservicePK Tjmsservice.id;
    
    public TjmsservicePK Tjmsservice.getId() {
        return this.id;
    }
    
    public void Tjmsservice.setId(TjmsservicePK id) {
        this.id = id;
    }
    
    @Transactional
    public void Tjmsservice.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Tjmsservice.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Tjmsservice attached = Tjmsservice.findTjmsservice(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Tjmsservice.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Tjmsservice.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Tjmsservice Tjmsservice.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Tjmsservice merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager Tjmsservice.entityManager() {
        EntityManager em = new Tjmsservice().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Tjmsservice.countTjmsservices() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Tjmsservice o", Long.class).getSingleResult();
    }
    
    public static List<Tjmsservice> Tjmsservice.findAllTjmsservices() {
        return entityManager().createQuery("SELECT o FROM Tjmsservice o", Tjmsservice.class).getResultList();
    }
    
    public static Tjmsservice Tjmsservice.findTjmsservice(TjmsservicePK id) {
        if (id == null) return null;
        return entityManager().find(Tjmsservice.class, id);
    }
    
    public static List<Tjmsservice> Tjmsservice.findTjmsserviceEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Tjmsservice o", Tjmsservice.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
