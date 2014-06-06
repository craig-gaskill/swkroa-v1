package com.cagst.swkroa.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cagst.swkroa.member.Membership;
import com.cagst.swkroa.member.MembershipRepository;

/**
 * Handles and retrieves the membership page(s) depending on the URI template.
 * 
 * @author Craig Gaskill
 * 
 * @version 1.0.0
 * 
 */
@Controller
public final class MembershipController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MembershipController.class);

	@Autowired
	private MembershipRepository membershipRepo;

	/**
	 * Handles and retrieves the Membership Home / Listing page.
	 * 
	 * @return The location and name of the page template.
	 */
	@RequestMapping(value = "/membership/home", method = RequestMethod.GET)
	public ModelAndView getMembershipListingPage() {
		LOGGER.info("Received request to show membership listing page.");

//		List<Membership> memberships = membershipRepo.getActiveMemberships();
//		List<MembershipModel> models = new ArrayList<MembershipModel>(memberships.size());
//		for (Membership membership : memberships) {
//			models.add(new MembershipModel(membership));
//		}
//
//		Collections.sort(models);

		ModelAndView mav = new ModelAndView("membership/index");
//		mav.addObject("memberships", models);

		return mav;
	}

	/**
	 * Handles and retrieves the Add Membership page.
	 * 
	 * @return The location and name of the page template.
	 */
	@RequestMapping(value = "/membership/add", method = RequestMethod.GET)
	public ModelAndView getMembershipAddPage() {
		LOGGER.info("Received request to show add membership page.");

		ModelAndView mav = new ModelAndView("membership/modify");
		mav.addObject("membershipId", 0L);

		return mav;
	}

	/**
	 * Handles and retrieves the Edit Membership page.
	 * 
	 * @param membershipId
	 *          A {@link long} that uniquely identifies the {@link Membership} to retrieve.
	 * 
	 * @return The location and name of the page template.
	 */
	@RequestMapping(value = "/membership/edit/{membershipId}", method = RequestMethod.GET)
	public ModelAndView getMembershipEditPage(final @PathVariable long membershipId) {
		LOGGER.info("Received request to show edit membership page.");

		ModelAndView mav = new ModelAndView("membership/modify");
		mav.addObject("membershipId", membershipId);

		return mav;
	}
}