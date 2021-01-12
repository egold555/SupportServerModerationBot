package org.golde.discordbot.supportserver.database;

import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.db.AbstractDBTranslation;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "deletedmessages")
@Entity

@AllArgsConstructor(onConstructor = @__(@Deprecated))
@NoArgsConstructor(onConstructor = @__(@Deprecated))
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class DeletedMessage extends AbstractDBTranslation {

	private static final long DELETION_TIME = 1000 * 60 * 60 * 24 * 7; //7 days
	private static final long serialVersionUID = -1041074304692260659L;

	@Id 
	long msgID;

	long user;

	String text;

	long timeSent;

	public DeletedMessage(long msgID, long user, String text) {
		this.msgID = msgID;
		this.user = user;
		this.text = text;
		this.timeSent = System.currentTimeMillis();
	}

	public static Long addMessage(ESSBot bot, DeletedMessage msgIn) {

		Session session = bot.getMySQL().openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			msgIn.hiddenBotInstance = bot;
			session.save(msgIn); 
			tx.commit();
			return msgIn.msgID;
		} 
		catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			e.printStackTrace(); 
			return null;
		} 
		finally {
			session.close(); 
		}

	}

	@Nullable
	public static DeletedMessage get(ESSBot bot, long idIn) {
		Session session =  bot.getMySQL().openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<DeletedMessage> criteriaQuery = criteriaBuilder.createQuery(DeletedMessage.class);
		Root<DeletedMessage> root = criteriaQuery.from(DeletedMessage.class);


		criteriaQuery.select(root)

		.where(
				criteriaBuilder.equal(root.get("msgID"), idIn)
				);


		Query<DeletedMessage> query = session.createQuery(criteriaQuery);
		List<DeletedMessage> results = query.getResultList();

		if(results.size() > 0) {
			return (DeletedMessage) results.get(0).setHiddenBotInstance(bot);
		}

		return null;
	}
	
	public boolean updateText(long idIn, String text) {
		
		DeletedMessage dm = get(hiddenBotInstance, idIn);
		
		if(dm == null) {
			return false;
		}

		dm.text = text;
		dm.update();
		return true;
		
	}

	public static List<DeletedMessage> getAllMessages(ESSBot bot) {

		Session session =  bot.getMySQL().openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<DeletedMessage> criteriaQuery = criteriaBuilder.createQuery(DeletedMessage.class);
		Root<DeletedMessage> root = criteriaQuery.from(DeletedMessage.class);


		criteriaQuery.select(root)

		.where(
				criteriaBuilder.lt(root.get("timeSent"), System.currentTimeMillis() - DELETION_TIME)
				);


		Query<DeletedMessage> query = session.createQuery(criteriaQuery);
		List<DeletedMessage> results = query.getResultList();

		return results;
	}

}
