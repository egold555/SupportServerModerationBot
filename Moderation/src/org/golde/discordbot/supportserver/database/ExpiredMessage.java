package org.golde.discordbot.supportserver.database;

import java.util.List;

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

@Table(name = "dupemessages")
@Entity

@AllArgsConstructor(onConstructor = @__(@Deprecated))
@NoArgsConstructor(onConstructor = @__(@Deprecated))
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ExpiredMessage extends AbstractDBTranslation {

	private static final long serialVersionUID = -1041074304692260659L;

	@Id 
	long id;

	long user;

	String hash;

	long issued;
	long expires;

	boolean hasExpired = false;

	public ExpiredMessage(long id, long user, String hash, long expires) {
		this.id = id;
		this.user = user;
		this.hash = hash;
		this.expires = expires;
		this.issued = System.currentTimeMillis();
	}

	public void setExpired() {
		this.hasExpired = true;
		update();
	}
	
	public static Long addMessage(ESSBot bot, ExpiredMessage msgIn) {

		Session session = bot.getMySQL().openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			msgIn.hiddenBotInstance = bot;
			session.save(msgIn); 
			tx.commit();
			return msgIn.id;
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

	public static List<ExpiredMessage> getAllExpiredMessages(ESSBot bot) {

		Session session =  bot.getMySQL().openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<ExpiredMessage> criteriaQuery = criteriaBuilder.createQuery(ExpiredMessage.class);
		Root<ExpiredMessage> root = criteriaQuery.from(ExpiredMessage.class);

		//If ( expires != null ) && ( expires > System.currentTimeMillis() ) && ( hasExpired != null ) && ( !hasExpired )
		criteriaQuery.select(root)

		.where(
				criteriaBuilder.and(
						criteriaBuilder.lt(root.get("expires"), System.currentTimeMillis()), 
						criteriaBuilder.isFalse(root.get("hasExpired"))
						)
				);


		Query<ExpiredMessage> query = session.createQuery(criteriaQuery);
		List<ExpiredMessage> results = query.getResultList();

		return results;
	}
	
	public static boolean hasUserSentThisHashBefore(ESSBot bot, long theUser, String theHash) {

		Session session =  bot.getMySQL().openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<ExpiredMessage> criteriaQuery = criteriaBuilder.createQuery(ExpiredMessage.class);
		Root<ExpiredMessage> root = criteriaQuery.from(ExpiredMessage.class);

		//If ( expires != null ) && ( expires > System.currentTimeMillis() ) && ( hasExpired != null ) && ( !hasExpired )
		criteriaQuery.select(root)

		.where(
				criteriaBuilder.and(
						criteriaBuilder.equal(root.get("user"), theUser),
						criteriaBuilder.equal(root.get("hash"), theHash), 
						criteriaBuilder.isFalse(root.get("hasExpired"))
						)
				);


		Query<ExpiredMessage> query = session.createQuery(criteriaQuery);
		List<ExpiredMessage> results = query.getResultList();

		return results.size() > 0;
	}
	
}
