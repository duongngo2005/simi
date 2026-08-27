import { useQuery } from "@tanstack/react-query";
import { consignmentApi } from "../api/consignmentApi";

export const useGetMyConsignments = () => {
  return useQuery({
    queryKey: ["my-consignments"],
    queryFn: () => consignmentApi.getMyConsignments(),
    select: (response) => response.body,
  });
};

export const useGetConsignmentDetail = (id: number) => {
  return useQuery({
    queryKey: ["consignment-detail", id],
    queryFn: () => consignmentApi.getConsignmentFullDetail(id),
    select: (res) => res.body,
    enabled: id > 0,
  });
};

export const useGetMySettlement = (consignmentId: number) => {
  return useQuery({
    queryKey: ["my-settlement", consignmentId],
    queryFn: () => consignmentApi.getMySettlements(),
    select: (res) => res.body?.find((s) => s.consignmentId === consignmentId) ?? null,
    enabled: consignmentId > 0,
  });
};

export const useGetMyDispositions = (consignmentId: number) => {
  return useQuery({
    queryKey: ["my-dispositions", consignmentId],
    queryFn: () => consignmentApi.getMyDispositions(consignmentId),
    select: (res) => res.body ?? [],
    enabled: consignmentId > 0,
  });
};