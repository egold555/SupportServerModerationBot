package org.golde.discordbot.supportserver.database;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.db.AbstractDBTranslation;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "infractions")
@Entity

@AllArgsConstructor(onConstructor = @__(@Deprecated))
@NoArgsConstructor(onConstructor = @__(@Deprecated))
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Offence extends AbstractDBTranslation {
	
	private static final long serialVersionUID = -3874637339376421662L;

	@Id long id;
	
	long offender;
	long moderator;
	ModAction type;
	@Nullable String reason;
	long issued;
	@Nullable Long expires;
	
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
	}
	
	public static Long addOffence(ESSBot bot, Offence offence) {

		Session session = bot.getMySQL().openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			offence.hiddenBotInstance = bot;
			long x = (long) session.save(offence); 
			tx.commit();
			return x;
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
		return employee;
	}
	
}
