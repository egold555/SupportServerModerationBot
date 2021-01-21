package org.golde.discordbot.website.db;

import java.util.List;

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
import org.hibernate.Session;
import org.hibernate.query.Query;

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
	
	public enum ModAction {
		WARN, KICK, BAN, UNBAN, MUTE, UNMUTE;
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
