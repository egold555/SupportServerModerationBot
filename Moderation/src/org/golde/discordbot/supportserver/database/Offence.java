package org.golde.discordbot.supportserver.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.db.AbstractDBTranslation;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@Table(name = "infractions")
@Entity

@AllArgsConstructor(onConstructor = @__(@Deprecated))
@NoArgsConstructor(onConstructor = @__(@Deprecated))
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Offence extends AbstractDBTranslation {

	private static final long serialVersionUID = -3874637339376421662L;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
	static {
		SDF.setTimeZone(TimeZone.getTimeZone("PST"));
	}

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	long offender;
	long moderator;

	@Enumerated(EnumType.STRING)
	ModAction type;

	@Nullable 
	String reason;
	long issued;

	@Nullable 
	Long expires;

	@Nullable Boolean hasExpired = null;

	//	@Deprecated
	//	public Offence(int id, long offender, long moderator, ModAction type, String reason, long issued, Long expires) {
	//		this.id = id;
	//		this.offender = offender;
	//		this.moderator = moderator;
	//		this.type = type;
	//		this.reason = reason;
	//		this.issued = issued;
	//		this.expires = expires;
	//	}

	public Offence(long offender, long moderator, ModAction type, String reason) {
		this(offender, moderator, type, reason, null);
	}

	public Offence(long offender, long moderator, ModAction type, String reason, Long expires) {
		this.offender = offender;
		this.moderator = moderator;
		this.type = type;
		this.reason = reason;
		this.issued = System.currentTimeMillis();
		this.expires = expires;
		if(expires != null) {
			hasExpired = false;
		}
	}

	public void setExpired() {
		this.hasExpired = true;
		update();
	}
	
	public String getAsCSVLine(Guild g) {
		StringBuilder b = new StringBuilder();
		
		b.append(SDF.format(new Date(issued)));
		b.append(',');
		
		String expiresStr = "NULL";
		if(expires != null) {
			expiresStr = SDF.format(new Date(expires));
		}
		
		b.append(expiresStr);
		b.append(',');
		
		Member modMemberObj = g.getMemberById(moderator);
		String moderatorStr = Long.toString(moderator);
		if(modMemberObj != null && modMemberObj.getUser() != null) {
			moderatorStr = modMemberObj.getUser().getName();
		}
		b.append(moderatorStr);
		b.append(',');
		
		b.append(type.name());
		b.append(',');
		
		String reasonString = "NULL";
		if(reason != null && !reason.isEmpty()) {
			reasonString = reason;
		}
		b.append(reasonString);
		b.append("\n");
		return b.toString();
	}

	public static Long addOffence(ESSBot bot, Offence offence) {

		Session session = bot.getMySQL().openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			offence.hiddenBotInstance = bot;
			session.save(offence); 
			tx.commit();
			return offence.id;
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

	public static Offence getExistingOffence(ESSBot bot, long id) {
		Session session =  bot.getMySQL().openSession();
		Offence employee = (Offence)session.get(Offence.class, id); 
		employee.hiddenBotInstance = bot;
		session.close();
		return employee;
	}

	public static List<Offence> getExpiredOffences(ESSBot bot) {

		Session session =  bot.getMySQL().openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Offence> criteriaQuery = criteriaBuilder.createQuery(Offence.class);
		Root<Offence> root = criteriaQuery.from(Offence.class);

		//If ( expires != null ) && ( expires > System.currentTimeMillis() ) && ( hasExpired != null ) && ( !hasExpired )
		criteriaQuery.select(root)

		.where(
				criteriaBuilder.and(
						criteriaBuilder.isNotNull(root.get("expires")), 
						criteriaBuilder.lt(root.get("expires"), System.currentTimeMillis()), 
						criteriaBuilder.isNotNull(root.get("hasExpired")), 
						criteriaBuilder.isFalse(root.get("hasExpired"))
						)
				);


		Query<Offence> query = session.createQuery(criteriaQuery);
		List<Offence> results = query.getResultList();

		return results;
	}

	public static List<Offence> getTotalOffencesByCategory(ESSBot bot, long offender, ModAction type) {

		Session session =  bot.getMySQL().openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Offence> criteriaQuery = criteriaBuilder.createQuery(Offence.class);
		Root<Offence> root = criteriaQuery.from(Offence.class);

		//If ( userId == dbUserID ) && ( type == > dbType )
		criteriaQuery.select(root)

		.where(
				criteriaBuilder.and(
						criteriaBuilder.equal(root.get("offender"), offender), 
						criteriaBuilder.equal(root.get("type"), type)
						)
				);


		Query<Offence> query = session.createQuery(criteriaQuery);
		List<Offence> results = query.getResultList();

		return results;
	}
	
	public static List<Offence> getAllOffences(ESSBot bot, long offender) {

		Session session =  bot.getMySQL().openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Offence> criteriaQuery = criteriaBuilder.createQuery(Offence.class);
		Root<Offence> root = criteriaQuery.from(Offence.class);

		//If ( userId == dbUserID ) && ( type == > dbType )
		criteriaQuery.select(root)

		.where(
				criteriaBuilder.equal(root.get("offender"), offender)
				);


		Query<Offence> query = session.createQuery(criteriaQuery);
		List<Offence> results = query.getResultList();

		return results;
	}

}
