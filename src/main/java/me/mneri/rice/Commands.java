/*
 * This file is part of Rice.
 * © Copyright Massimo Neri 2014 <hello@mneri.me>
 *
 * Rice is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rice is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Rice. If not, see <http://www.gnu.org/licenses/>.
*/

package me.mneri.rice;

public class Commands {
	//@formatter:off
	public static final String ADMIN        = "ADMIN";
	public static final String AUTHENTICATE = "AUTHENTICATE";
	public static final String AWAY         = "AWAY";
	public static final String CAP          = "CAP";
	public static final String ERROR        = "ERROR";
	public static final String HELP         = "HELP";
	public static final String INFO         = "INFO";
	public static final String INVITE       = "INVITE";
	public static final String ISON         = "ISON";
	public static final String JOIN         = "JOIN";
	public static final String KICK         = "KICK";
	public static final String KILL         = "KILL";
	public static final String LINKS        = "LINKS";
	public static final String LIST         = "LIST";
	public static final String LUSERS       = "LUSERS";
	public static final String MODE         = "MODE";
	public static final String MOTD         = "MOTD";
	public static final String NAMES        = "NAMES";
	public static final String NICK         = "NICK";
	public static final String NOTICE       = "NOTICE";
	public static final String OPER         = "OPER";
	public static final String PART         = "PART";
	public static final String PASS         = "PASS";
	public static final String PING         = "PING";
	public static final String PONG         = "PONG";
	public static final String PRIVMSG      = "PRIVMSG";
	public static final String QUIT         = "QUIT";
	public static final String SERVLIST     = "SERVLIST";
	public static final String SQUERY       = "SQUERY";
	public static final String SQUIT        = "SQUIT";
	public static final String STATS        = "STATS";
	public static final String TIME         = "TIME";
	public static final String TOPIC        = "TOPIC";
	public static final String TRACE        = "TRACE";
	public static final String USER         = "USER";
	public static final String USERHOST     = "USERHOST";
	public static final String USERS        = "USERS";
	public static final String VERSION      = "VERSION";
	public static final String WALLOPS      = "WALLOPS";
	public static final String WHO          = "WHO";
	public static final String WHOIS        = "WHOIS";
	public static final String WHOWAS       = "WHOWAS";

	public static final String RPL_WELCOME           = "001";
	public static final String RPL_YOURHOST          = "002";
	public static final String RPL_CREATED           = "003";
	public static final String RPL_MYINFO            = "004";
	public static final String RPL_BOUNCE            = "005"; // There is a conflict between RPL_BOUNCE
	public static final String RPL_ISUPPORT          = "005"; // and RPL_ISUPPORT
	public static final String RPL_TRACELINK         = "200";
	public static final String RPL_TRACECONNECTING   = "201";
	public static final String RPL_TRACEHANDSHAKE    = "202";
	public static final String RPL_TRACEUNKNOWN      = "203";
	public static final String RPL_TRACEOPERATOR     = "204";
	public static final String RPL_TRACEUSER         = "205";
	public static final String RPL_TRACESERVER       = "206";
	public static final String RPL_TRACESERVICE      = "207";
	public static final String RPL_TRACENEWTYPE      = "208";
	public static final String RPL_TRACECLASS        = "209";
	public static final String RPL_TRACERECONNECT    = "210";
	public static final String RPL_STATSLINKINFO     = "211";
	public static final String RPL_STATSCOMMANDS     = "212";
	public static final String RPL_STATSCLINE        = "213";
	public static final String RPL_STATSNLINE        = "214";
	public static final String RPL_STATSILINE        = "215";
	public static final String RPL_STATSKLINE        = "216";
	public static final String RPL_STATSQLINE        = "217";
	public static final String RPL_STATSYLINE        = "218";
	public static final String RPL_ENDOFSTATS        = "219";
	public static final String RPL_UMODEIS           = "221";
	public static final String RPL_SERVICEINFO       = "231";
	public static final String RPL_ENDOFSERVICES     = "232";
	public static final String RPL_SERVICE           = "233";
	public static final String RPL_SERVLIST          = "234";
	public static final String RPL_SERVLISTEND       = "235";
	public static final String RPL_STATSLLINE        = "241";
	public static final String RPL_STATSUPTIME       = "242";
	public static final String RPL_STATSOLINE        = "243";
	public static final String RPL_STATSHLINE        = "244";
	public static final String RPL_LUSERCLIENT       = "251";
	public static final String RPL_LUSEROP           = "252";
	public static final String RPL_LUSERUNKNOWN      = "253";
	public static final String RPL_LUSERCHANNELS     = "254";
	public static final String RPL_LUSERME           = "255";
	public static final String RPL_ADMINME           = "256";
	public static final String RPL_ADMINLOC1         = "257";
	public static final String RPL_ADMINLOC2         = "258";
	public static final String RPL_ADMINEMAIL        = "259";
	public static final String RPL_TRACELOG          = "261";
	public static final String RPL_TRACEEND          = "262";
	public static final String RPL_TRYAGAIN          = "263";
	public static final String RPL_NONE              = "300";
	public static final String RPL_AWAY              = "301";
	public static final String RPL_USERHOST          = "302";
	public static final String RPL_ISON              = "303";
	public static final String RPL_UNAWAY            = "305";
	public static final String RPL_NOWAWAY           = "306";
	public static final String RPL_WHOISUSER         = "311";
	public static final String RPL_WHOISSERVER       = "312";
	public static final String RPL_WHOISOPERATOR     = "313";
	public static final String RPL_WHOWASUSER        = "314";
	public static final String RPL_ENDOFWHO          = "315";
	public static final String RPL_WHOISCHANOP       = "316";
	public static final String RPL_WHOISIDLE         = "317";
	public static final String RPL_ENDOFWHOIS        = "318";
	public static final String RPL_WHOISCHANNELS     = "319";
	public static final String RPL_LISTSTART         = "321";
	public static final String RPL_LIST              = "322";
	public static final String RPL_LISTEND           = "323";
	public static final String RPL_CHANNELMODEIS     = "324";
	public static final String RPL_UNIQOPIS          = "325";
	public static final String RPL_NOTOPIC           = "331";
	public static final String RPL_TOPIC             = "332";
	public static final String RPL_TOPICINFO         = "333";
	public static final String RPL_INVITING          = "341";
	public static final String RPL_SUMMONING         = "342";
	public static final String RPL_INVITELIST        = "346";
	public static final String RPL_ENDOFINVITELIST   = "347";
	public static final String RPL_EXCEPTLIST        = "348";
	public static final String RPL_ENDOFEXCEPTLIST   = "349";
	public static final String RPL_VERSION           = "351";
	public static final String RPL_WHOREPLY          = "352";
	public static final String RPL_NAMREPLY          = "353";
	public static final String RPL_KILLDONE          = "361";
	public static final String RPL_CLOSING           = "362";
	public static final String RPL_CLOSEEND          = "363";
	public static final String RPL_LINKS             = "364";
	public static final String RPL_ENDOFLINKS        = "365";
	public static final String RPL_ENDOFNAMES        = "366";
	public static final String RPL_BANLIST           = "367";
	public static final String RPL_ENDOFBANLIST      = "368";
	public static final String RPL_ENDOFWHOWAS       = "369";
	public static final String RPL_INFO              = "371";
	public static final String RPL_MOTD              = "372";
	public static final String RPL_INFOSTART         = "373";
	public static final String RPL_ENDOFINFO         = "374";
	public static final String RPL_MOTDSTART         = "375";
	public static final String RPL_ENDOFMOTD         = "376";
	public static final String RPL_YOUREOPER         = "381";
	public static final String RPL_REHASHING         = "382";
	public static final String RPL_YOURESERVICE      = "383";
	public static final String RPL_MYPORTIS          = "384";
	public static final String RPL_TIME              = "391";
	public static final String RPL_USERSSTART        = "392";
	public static final String RPL_USERS             = "393";
	public static final String RPL_ENDOFUSERS        = "394";
	public static final String RPL_NOUSERS           = "395";
	public static final String ERR_NOSUCHNICK        = "401";
	public static final String ERR_NOSUCHSERVER      = "402";
	public static final String ERR_NOSUCHCHANNEL     = "403";
	public static final String ERR_CANNOTSENDTOCHAN  = "404";
	public static final String ERR_TOOMANYCHANNELS   = "405";
	public static final String ERR_WASNOSUCHNICK     = "406";
	public static final String ERR_TOOMANYTARGETS    = "407";
	public static final String ERR_NOORIGIN          = "409";
	public static final String ERR_NORECIPIENT       = "411";
	public static final String ERR_NOTEXTTOSEND      = "412";
	public static final String ERR_NOTOPLEVEL        = "413";
	public static final String ERR_WILDTOPLEVEL      = "414";
	public static final String ERR_BADMASK           = "415";
	public static final String ERR_UNKNOWNCOMMAND    = "421";
	public static final String ERR_NOMOTD            = "422";
	public static final String ERR_NOADMININFO       = "423";
	public static final String ERR_FILEERROR         = "424";
	public static final String ERR_NONICKNAMEGIVEN   = "431";
	public static final String ERR_ERRONEUSNICKNAME  = "432";
	public static final String ERR_NICKNAMEINUSE     = "433";
	public static final String ERR_NICKCOLLISION     = "436";
	public static final String ERR_UNAVAILRESOURCE   = "437";
	public static final String ERR_USERNOTINCHANNEL  = "441";
	public static final String ERR_NOTONCHANNEL      = "442";
	public static final String ERR_USERONCHANNEL     = "443";
	public static final String ERR_NOLOGIN           = "444";
	public static final String ERR_SUMMONDISABLED    = "445";
	public static final String ERR_USERSDISABLED     = "446";
	public static final String ERR_NOTREGISTERED     = "451";
	public static final String ERR_NEEDMOREPARAMS    = "461";
	public static final String ERR_ALREADYREGISTRED  = "462";
	public static final String ERR_NOPERMFORHOST     = "463";
	public static final String ERR_PASSWDMISMATCH    = "464";
	public static final String ERR_YOUREBANNEDCREEP  = "465";
	public static final String ERR_YOUWILLBEBANNED   = "466";
	public static final String ERR_KEYSET            = "467";
	public static final String ERR_LINKCHANNEL       = "470";
	public static final String ERR_KICKEDFROMCHAN    = "470";
	public static final String ERR_CHANNELISFULL     = "471";
	public static final String ERR_UNKNOWNMODE       = "472";
	public static final String ERR_INVITEONLYCHAN    = "473";
	public static final String ERR_BANNEDFROMCHAN    = "474";
	public static final String ERR_BADCHANNELKEY     = "475";
	public static final String ERR_BADCHANMASK       = "476";
	public static final String ERR_NOCHANMODES       = "477";
	public static final String ERR_BANLISTFULL       = "478";
	public static final String ERR_BADCHANNAME       = "479";
	public static final String ERR_NOPRIVILEGES      = "481";
	public static final String ERR_CHANOPRIVSNEEDED  = "482";
	public static final String ERR_CANTKILLSERVER    = "483";
	public static final String ERR_RESTRICTED        = "484";
	public static final String ERR_UNIQOPPRIVSNEEDED = "485";
	public static final String ERR_NOOPERHOST        = "491";
	public static final String ERR_NOSERVICEHOST     = "492";
	public static final String ERR_UMODEUNKNOWNFLAG  = "501";
	public static final String ERR_USERSDONTMATCH    = "502";
	public static final String RPL_LOGGEDIN          = "900";
	public static final String RPL_LOGGEDOUT         = "901";
	public static final String ERR_NICKLOCKED        = "902";
	public static final String RPL_SASLSUCCESS       = "903";
	public static final String ERR_SASLFAIL          = "904";
	public static final String ERR_SASLTOOLONG       = "905";
	public static final String ERR_SASLABORTED       = "906";
	public static final String ERR_SASLALREADY       = "907";
	public static final String RPL_SASLMECH          = "908";
	//@formatter:on
}
