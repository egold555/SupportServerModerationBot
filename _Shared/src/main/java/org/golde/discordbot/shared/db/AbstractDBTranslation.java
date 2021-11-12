package org.golde.discordbot.shared.db;

import java.io.Serializable;

import javax.persistence.Transient;

import org.golde.discordbot.shared.ESSBot;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
public class AbstractDBTranslation implements Serializable {

	private static final long serialVersionUID = -3495734806500219414L;

	@ToString.Exclude 
	@EqualsAndHashCode.Exclude
	@Transient
	protected ESSBot hiddenBotInstance;
	
	public AbstractDBTranslation setHiddenBotInstance(ESSBot hiddenBotInstance) {
		this.hiddenBotInstance = hiddenBotInstance;
		return this;
	}
	
	public final void update() {
		Session session = hiddenBotInstance.getMySQL().openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.update(this); 
			tx.commit();
		} 
		catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			e.printStackTrace(); 
		} 
		finally {
			session.close(); 
		}
	}

	public final void delete(){
		Session session =  hiddenBotInstance.getMySQL().openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.delete(this); 
			tx.commit();
		}
		catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			e.printStackTrace(); 
		} 
		finally {
			session.close(); 
		}
	}

}